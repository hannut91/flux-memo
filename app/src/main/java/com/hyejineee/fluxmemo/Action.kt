package com.hyejineee.fluxmemo

enum class ActionType{
    GET_MEMO, GET_MEMOS, UPDATE_MEMO, DELETE_MEMO
}
class Action(val type:ActionType, vararg val data:Any)
