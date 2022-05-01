package com.bupp.wood_spoon_chef.presentation.features.main.my_dishes

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import com.bupp.wood_spoon_chef.presentation.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_chef.databinding.FragmentMyDishesBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.main.my_dishes.dialogs.DishUpdatedDialog
import com.bupp.wood_spoon_chef.presentation.features.main.my_dishes.dialogs.HideDishDialog
import com.bupp.wood_spoon_chef.presentation.features.main.my_dishes.dialogs.MyDishItemMenuBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.main.my_dishes.dialogs.WelcomeDialog
import com.bupp.wood_spoon_chef.presentation.features.main.single_dish.SingleDishDialog
import com.bupp.wood_spoon_chef.presentation.features.new_dish.NewDishActivity
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class MyDishesFragment : BaseFragment(R.layout.fragment_my_dishes),
    DishUpdatedDialog.DishUpdateListener, MyDishesAdapter.MyDishesAdapterListener,
    HideDishDialog.HideDishDialogListener, WelcomeDialog.WelcomeDialogListener,
    MyDishItemMenuBottomSheet.DishChooserListener, HeaderView.HeaderViewListener {

    //activityForResult
    private val afterNewDishResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.d(TAG, "Activity For Result - new dish")
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                initPage()
            }
        }


    var binding: FragmentMyDishesBinding? = null
    private var adapter: MyDishesAdapter? = null
    val viewModel by sharedViewModel<MyDishesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMyDishesBinding.bind(view)

        initPage()
        initObservers()
    }

    private fun initPage() {
        viewModel.getMyDishes()

        with(binding!!) {
            myDishesFragSearchInput.addTextChangedListener(object : SimpleTextWatcher() {
                override fun afterTextChanged(s: Editable) {
                    super.afterTextChanged(s)
                    viewModel.filterList(s.toString())
                }
            })

            myDishesFragList.layoutManager = LinearLayoutManager(context)
            adapter = MyDishesAdapter(requireContext(), this@MyDishesFragment)
            myDishesFragList.adapter = adapter
            mainActHeaderView.setHeaderViewListener(this@MyDishesFragment)
        }
    }

    private fun initObservers() {
        viewModel.progressData.observe(viewLifecycleOwner, {
            handleProgressBar(it)
        })
        viewModel.getDishesEvent.observe(viewLifecycleOwner, { event ->
            event?.let {
                if (it.isEmpty()) {
                    loadEmptyList()
                } else {
                    loadMyDishes(it)
                }
            }
        })
        viewModel.filterDishesEvent.observe(viewLifecycleOwner, { event ->
            event?.let {
                if (it.isEmpty()) {
                    loadFilterEmptyList()
                } else {
                    loadMyDishes(it)
                }
            }
        })
        viewModel.itemMenuEvent.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                val dishChooserDialog = MyDishItemMenuBottomSheet.newInstance(it.status)
                dishChooserDialog.show(childFragmentManager, Constants.DISH_CHOOSER_DIALOG_TAG)
            }
        })
        viewModel.dishUpdateEvent.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { event ->
                DishUpdatedDialog.newInstance(event.title, event.body)
                    .show(childFragmentManager, Constants.NEW_DISH_DONE_DIALOG)
            }
        })
        viewModel.editDishEvent.observe(viewLifecycleOwner, { event ->
            event?.getContentIfNotHandled()?.let {
                afterNewDishResult.launch(
                    Intent(
                        requireContext(),
                        NewDishActivity::class.java
                    ).putExtra(NewDishActivity.EDIT_DISH_PARAM, it)
                )
            }
        })
        viewModel.errorEvent.observe(viewLifecycleOwner, {
            handleErrorEvent(it, binding?.root)
        })
    }

    private fun loadMyDishes(dishes: List<Dish>?) {
        with(binding!!) {
            myDishesFragDishesLayout.visibility = VISIBLE
            myDishesFragEmptyLayout.visibility = GONE
            myDishesFragSearchEmpty.visibility = GONE

            myDishesFragList.visibility = VISIBLE
            adapter?.submitList(dishes)
        }
    }

    private fun loadEmptyList() {
        with(binding!!) {
            myDishesFragName.text = viewModel.getGreetingsText()
            myDishesFragDishesLayout.visibility = GONE
            myDishesFragEmptyLayout.visibility = VISIBLE
            myDishesFragAddDish.setOnClickListener { addNewDish() }
        }
    }

    private fun loadFilterEmptyList() {
        with(binding!!) {
            myDishesFragList.visibility = GONE
            myDishesFragSearchEmpty.visibility = VISIBLE
        }
    }

    override fun onHeaderAddClick() {
        addNewDish()
    }

    private fun addNewDish() {
        afterNewDishResult.launch(Intent(requireContext(), NewDishActivity::class.java))
    }

    override fun onContinueClick() {

    }

    override fun onNotNowClick() {

    }

    override fun onItemMenuClick(selected: Dish?) {
        viewModel.onItemMenuClick(selected)
    }

    override fun onDishClicked(dish: Dish) {
        val singleDishDialog = dish.id?.let { SingleDishDialog.newInstance(it) }
        singleDishDialog?.show(childFragmentManager, Constants.SINGLE_DISH_TAG)
    }

    override fun onDraftClicked(dish: Dish) {
        afterNewDishResult.launch(
            Intent(requireContext(), NewDishActivity::class.java).putExtra(
                NewDishActivity.EDIT_DISH_PARAM,
                dish.id
            )
        )
    }

    override fun onDishChooserResult(result: Int) {
        when (result) {
            Constants.DISH_CHOOSER_EDIT -> {
                viewModel.onEditDishClick()
            }
            Constants.DISH_CHOOSER_HIDE -> {
                val hideDishDialog = HideDishDialog(this)
                hideDishDialog.show(childFragmentManager, Constants.HIDE_DISH_DIALOG_TAG)
            }
            Constants.DISH_CHOOSER_ACTIVATE -> {
                viewModel.publishDish()
            }
            Constants.DISH_CHOOSER_UNPUBLISH -> {
                viewModel.unPublishDish()
            }
            Constants.DISH_CHOOSER_DUPLICATE -> {
                viewModel.duplicateDish()
            }
        }
    }

    override fun onDishHide() {
        viewModel.hideDish()
    }

    override fun clearClassVariables() {
        binding = null
        adapter = null
    }

    companion object {
        const val TAG = "wowMyDishesFrag"

        fun newInstance() = MyDishesFragment()

    }

    override fun onDishUpdateDialogDismiss() {
        initPage()
    }

}
