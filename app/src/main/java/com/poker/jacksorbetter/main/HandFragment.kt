 package com.poker.jacksorbetter.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.settings.SettingsUtils
import timber.log.Timber

/**
 * A fragment representing a list of Items.
 */
class HandFragment : Fragment(){

    companion object {

        @JvmStatic
        fun newInstance() = HandFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var handAdapter: HandsAdapter? = null
    private var payAdapter: HandPayAdapter? = null
    private var layManager: FlexboxLayoutManager? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.gameState.observe(viewLifecycleOwner,  { gameState ->
            if (gameState == MainViewModel.GameState.START){
                handAdapter?.setState(State.CARD_BACK)
                payAdapter?.unhighlightEvals()
            }
        })

        viewModel.bet.observe(viewLifecycleOwner,  { bet ->
            Timber.d("bet: $bet")
            payAdapter?.setBetAmount(bet)
            handAdapter?.setBetAmount(bet)
        })

        viewModel.numberOfHands.observe(viewLifecycleOwner,  { numHands ->
            Timber.d("number of hands: $numHands")
            handAdapter?.setState(State.CARD_BACK)
            handAdapter?.initHands(numHands - 1)
            layManager?.scrollToPosition(0)
        })

        viewModel.hands.observe(viewLifecycleOwner,  { hands ->
            hands?.let {
                Timber.d("data $hands")
                val nonMainHands = hands.subList(1, hands.size)
                handAdapter?.setHands(nonMainHands)
            }
        })

        viewModel.handEvals.observe(viewLifecycleOwner,  { evals ->
            if(evals != null && evals.isNotEmpty()){
                Timber.d("evals $evals")
                handAdapter?.setEvals(evals)
                payAdapter?.highlightEvals(evals.toSet())
                handAdapter?.setState(State.FLIP)
            }
        })

        viewModel.cardsHeld.observe(viewLifecycleOwner,  { held ->
            handAdapter?.setHold(held)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        layManager = FlexboxLayoutManager(requireContext())
        layManager?.flexWrap = FlexWrap.WRAP
        layManager?.flexDirection = FlexDirection.ROW
        layManager?.justifyContent = JustifyContent.CENTER
        layManager?.alignItems = AlignItems.CENTER

        val layManager2 = FlexboxLayoutManager(requireContext())
        layManager2.flexWrap = FlexWrap.NOWRAP
        layManager2.flexDirection = FlexDirection.ROW
        layManager2.justifyContent = JustifyContent.CENTER
        layManager2.alignItems = AlignItems.CENTER

        // sets up hand(s) recycler view
        handAdapter =  HandsAdapter()
        handAdapter?.setHasStableIds(true)
        val handRecycler = view.findViewById<RecyclerView>(R.id.list)
        with(handRecycler) {
            layoutManager = layManager
            adapter = handAdapter
            setItemViewCacheSize(100)
        }

        // sets up paytable recycler view
        payAdapter = HandPayAdapter(requireContext())
        payAdapter?.setHasStableIds(true)
        val payRecycler = view.findViewById<RecyclerView>(R.id.paylist)
        with(payRecycler) {
            layoutManager = layManager2
            adapter = payAdapter
        }

        if(SettingsUtils.getNumHands() >= 2){
            handAdapter?.initHands(SettingsUtils.getNumHands() - 1)
        }

        return view
    }
}