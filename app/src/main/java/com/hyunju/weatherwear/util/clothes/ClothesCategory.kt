package com.hyunju.weatherwear.util.clothes

import androidx.annotation.DrawableRes
import com.hyunju.weatherwear.R

object ClothesList {

    val temperaturesHigh = arrayListOf(
        Clothes.SLEEVELESS, // 민소매
        Clothes.SHORT_SLEEVES, // 반팔
        Clothes.SHORT_PANTS, // 반바지
        Clothes.SKIRT, // 치마
    )

    val temperatures23 = arrayListOf(
        Clothes.SLEEVELESS, // 반팔
        Clothes.SHIRT, // 셔츠
        Clothes.SHORT_PANTS, // 반바지
        Clothes.PANTS, // 긴바지
        Clothes.SKIRT, // 치마
    )

    val temperatures20 = arrayListOf(
        Clothes.BLOUSE, // 블라우스
        Clothes.LONG_SLEEVES, // 긴팔
        Clothes.SHIRT, // 셔츠
        Clothes.PANTS, // 면바지
        Clothes.JEAN_BLUE, // 청바지
        Clothes.SKIRT, // 치마
    )

    val temperatures17 = arrayListOf(
        Clothes.CARDIGAN, // 가디건
        Clothes.SWEATER_SHIRT, // 맨투맨
        Clothes.HOODIE, // 후드티
        Clothes.SHIRT, // 셔츠
        Clothes.PANTS, // 긴바지
        Clothes.JEAN_BLUE, // 청바지
    )

    val temperatures12 = arrayListOf(
        Clothes.JACKET, // 자켓
        Clothes.LEATHER_JACKET, // 가죽자켓
        Clothes.CARDIGAN, // 가디건
        Clothes.SWEATER_SHIRT, // 맨투맨
        Clothes.HOODIE, // 후드티
        Clothes.SWEATER, // 니트
        Clothes.SLACKS, // 슬랙스
        Clothes.PANTS, // 긴바지
        Clothes.JEAN_BLUE // 청바지
    )

    val temperatures9 = arrayListOf(
        Clothes.TRENCH_COAT, // 트렌치코트
        Clothes.JACKET, // 자켓
        Clothes.LEATHER_JACKET, // 가죽자켓
        Clothes.CARDIGAN, // 가디건
        Clothes.SWEATER_SHIRT, // 맨투맨
        Clothes.HOODIE, // 후드티
        Clothes.SWEATER, // 니트
        Clothes.SLACKS, // 슬랙스
        Clothes.PANTS, // 긴바지
        Clothes.JEAN_BLUE // 청바지
    )

    val temperatures5 = arrayListOf(
        Clothes.COAT, // 울코트
        Clothes.CARDIGAN, // 가디건
        Clothes.SWEATER_SHIRT, // 맨투맨
        Clothes.HOODIE, // 후드티
        Clothes.SWEATER, // 니트
        Clothes.SLACKS, // 슬랙스
        Clothes.PANTS, // 긴바지
        Clothes.JEAN_BLUE // 청바지
    )

    val temperaturesLow = arrayListOf(
        Clothes.LONG_PADDING, // 롱패딩
        Clothes.SHORT_PADDING, // 패딩
        Clothes.COAT, // 울코트
        Clothes.CARDIGAN, // 가디건
        Clothes.SWEATER_SHIRT, // 맨투맨
        Clothes.HOODIE, // 후드티
        Clothes.SWEATER, // 니트
        Clothes.SLACKS, // 슬랙스
        Clothes.PANTS, // 긴바지
        Clothes.JEAN_BLUE, // 청바지
        Clothes.MUFFLER // 목도리
    )
}

enum class Clothes(
    val text: String,
    @DrawableRes val image: Int
) {

    CARDIGAN("가디건", R.drawable.clothes_cardigan),

    COAT("울코트", R.drawable.clothes_coat_blue),
    TRENCH_COAT("트렌치코트", R.drawable.clothes_trench_coat),

    JACKET("자켓", R.drawable.clothes_jacket),
    LEATHER_JACKET("가죽자켓", R.drawable.clothes_leather_jacket),

    JEAN_BLUE("청바지", R.drawable.clothes_jean),
    PANTS("긴바지", R.drawable.clothes_long_pants),
    SHORT_PANTS("반바지", R.drawable.clothes_short_pants_yellow),
    SLACKS("슬랙스", R.drawable.clothes_slacks),

    LONG_PADDING("롱패딩", R.drawable.clothes_long_padding),
    SHORT_PADDING("패딩", R.drawable.clothes_padding_orange),

    SHORT_SLEEVES("반팔", R.drawable.clothes_short_sleeves_purple),
    LONG_SLEEVES("긴팔", R.drawable.clothes_long_sleeves),
    SLEEVELESS("민소매", R.drawable.clothes_sleeveless),
    BLOUSE("블라우스", R.drawable.clothes_blouse),
    SHIRT("셔츠", R.drawable.clothes_shirt),

    HOODIE("후드티", R.drawable.clothes_hoodie_red),
    SWEATER_SHIRT("맨투맨", R.drawable.clothes_sweater_shirt),
    SWEATER("니트", R.drawable.clothes_sweater),

    SKIRT("치마", R.drawable.clothes_mini_skirt),

    MUFFLER("목도리", R.drawable.clothes_scarf)

}