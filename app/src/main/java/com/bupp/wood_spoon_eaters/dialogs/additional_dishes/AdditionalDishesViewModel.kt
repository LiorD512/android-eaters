//package com.bupp.wood_spoon_eaters.dialogs.additional_dishes
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
//import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info.SingleDishInfoViewModel
//import com.bupp.wood_spoon_eaters.managers.OrderManager
//import com.bupp.wood_spoon_eaters.model.Dish
//import com.bupp.wood_spoon_eaters.model.FullDish
//import java.util.ArrayList
//
//class AdditionalDishesViewModel(val orderManager: OrderManager) : ViewModel() {
//
//    ///copy
//    val fullDish = MutableLiveData<FullDish>()
//
//    val dishlist : List<Dish> = emptyList()
//
//    sealed class NavigationEvent{
//        data class UpdateNewDish(val dish: Dish):NavigationEvent()
//    }
//
//    val navigationEvent = SingleLiveEvent<NavigationEvent>()
//
//    fun initDishesList(dishes: ArrayList<Dish>) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    fun setDish(dish : Dish){
////        this.dish.postValue(dish)
//    }
//
//    fun addDish(dishId: Long){
//       val s = dishlist.find { it.id == dishId }
//        s?.let {
//            navigationEvent.postValue(NavigationEvent.UpdateNewDish(it))
//        }
//    }
//
//    val additionalDishesEvent: SingleLiveEvent<SingleDishInfoViewModel.AdditionalDishesEvent> = SingleLiveEvent()
//    fun initAdditionalDishesList() {
//        fullDish.value?.let {
//            additionalDishesEvent.postValue(
//                SingleDishInfoViewModel.AdditionalDishesEvent(
//                    orderManager.curOrderResponse?.orderItems,
//                    it.cook.dishes
//                )
//            )
//        }
//    }
//
//
//}