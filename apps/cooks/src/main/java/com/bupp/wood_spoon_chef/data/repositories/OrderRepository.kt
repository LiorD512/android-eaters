package com.bupp.wood_spoon_chef.data.repositories

import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.repositories.base_repos.OrderRepositoryImp

class OrderRepository(service: ApiService, responseHandler: ResponseHandler): OrderRepositoryImp(service,responseHandler)