package com.bupp.wood_spoon_chef.data.repositories.base_repos

import com.bupp.wood_spoon_chef.data.remote.model.MetaDataModel
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult

interface BaseMetaDataRepository {
    suspend fun getMetaData(): ResponseResult<MetaDataModel>
}
