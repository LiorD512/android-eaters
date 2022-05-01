package com.bupp.wood_spoon_chef.data.repositories

import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.repositories.base_repos.EventRepositoryImp

class EventRepository(service: ApiService, responseHandler: ResponseHandler): EventRepositoryImp(service,responseHandler)