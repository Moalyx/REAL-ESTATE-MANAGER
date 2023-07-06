package com.tuto.realestatemanager.ui.detail

sealed class DetailViewAction{
    object NavigateToEditActivity : DetailViewAction()
    object NavigateToMainActivity : DetailViewAction()

}
