package com.tuto.realestatemanager.ui.list

sealed class ListViewAction{
    object NavigateToCreateActvity : ListViewAction()
    object NavigateToDetailActivity : ListViewAction()
}
