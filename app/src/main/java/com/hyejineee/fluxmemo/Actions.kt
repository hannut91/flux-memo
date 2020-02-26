package com.hyejineee.fluxmemo

enum class ActionType{
    GET_MEMO, GET_MEMOS
}
class Actions(val type:ActionType, val data:Any)

