package com.example.myfirstapp.data.Enums

import com.example.myfirstapp.R

enum class CardType(val imageRes: Int) {
    VISA(R.drawable.visa),
    MASTERCARD(R.drawable.mastercard),
    AMEX(R.drawable.amex),
    PSB(R.drawable.psb),
    SBER(R.drawable.sberbank),
    TINKOFF(R.drawable.tincoff),
    DISCOVER(R.drawable.discover),
    UNIONPAY(R.drawable.unionpay),
    JCB(R.drawable.jcb),
    OTHER(R.drawable.add_card_icon);
}
