package com.tuto.realestatemanager.ui.editproperty

import com.tuto.realestatemanager.ui.detail.DetailViewAction

sealed class EditViewAction{
    object NavigateFromEditToDetailActivity : EditViewAction()
}
