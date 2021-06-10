package com.poker.jacksorbetter

import Card
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.poker.jacksorbetter.cardgame.Evaluate
import com.poker.jacksorbetter.main.MainViewModel
import timber.log.Timber

/**
 * A fragment representing a list of Items.
 */
class HandFragment : Fragment(), HandAdapter.Callback {

    private lateinit var viewModel: MainViewModel

    private var adapterHandData = mutableListOf<MutableList<Card>>()
    private var adapterEvalData = mutableListOf<Evaluate.Hand>()

    private var mAdapter: HandAdapter? = null
    private var payAdapter: HandPayAdapter? = null

    private var layManager: FlexboxLayoutManager? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        viewModel.gameState.observe(viewLifecycleOwner, Observer { gameState ->
            if (gameState == MainViewModel.GameState.START){
                mAdapter?.setState(State.CARD_BACK)
                payAdapter?.unhighlightEvals()
            }
        })

        viewModel.bet.observe(viewLifecycleOwner, Observer { bet ->
            Timber.d("bet: $bet")
            payAdapter?.setBetAmount(bet)
            mAdapter?.setBetAmount(bet)
        })

        viewModel.numberOfHands.observe(viewLifecycleOwner, Observer { numHands ->
            Timber.d("number of hands: $numHands")
            emptyHand(numHands - 1)
            layManager?.scrollToPosition(0)
        })

        viewModel.hands.observe(viewLifecycleOwner, Observer { hands ->
            hands?.let {
                val nonMainHands = hands.subList(1, hands.size)
                Timber.d("data $hands")
                setHandData(nonMainHands)
            }
        })

        viewModel.handEvals.observe(viewLifecycleOwner, Observer { evals ->
            evals?.let {
                Timber.d("evals $evals")
                setEvalData(evals)
            }
        })

        viewModel.cardsHeld.observe(viewLifecycleOwner, Observer { held ->
            mAdapter?.hold(held)
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

        mAdapter =  HandAdapter(adapterHandData, mutableListOf(), adapterEvalData, 1, State.CARD_BACK)
        val recycler = view.findViewById<RecyclerView>(R.id.list)
        with(recycler) {
            layoutManager = layManager
            adapter = mAdapter
        }

        payAdapter = HandPayAdapter(requireContext())
        val recycler2 = view.findViewById<RecyclerView>(R.id.paylist)
        with(recycler2) {
            layoutManager = layManager2
            adapter = payAdapter
        }
      
        return view
    }

    private fun setHandData(data: MutableList<MutableList<Card>>) {
        adapterHandData = data
        mAdapter?.setHands(adapterHandData)
    }

    private fun setEvalData(evals: MutableList<Evaluate.Hand>) {
        adapterEvalData = evals
        mAdapter?.setEvals(adapterEvalData)
        payAdapter?.highlightEvals(adapterEvalData.toSet())
        Timber.d("setting eval data: $evals")
        mAdapter?.setState(State.FLIP)
    }

    private fun emptyHand(numHands: Int) {
        adapterHandData = MutableList(numHands){mutableListOf(Card(), Card(), Card(), Card(), Card())}
        mAdapter?.setState(State.CARD_BACK)
        mAdapter?.setHands(adapterHandData)
    }

    companion object {

        @JvmStatic
        fun newInstance() = HandFragment()
    }

    override fun onComplete() {
        // todo enable button viewmodel
    }
}