package ru.efremovkirill.tryrunner.presentation.utils

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun View.appear(
    before: () -> Unit = {},
    after: () -> Unit = {},
    animDuration: Long = 300L
) {
    before()
    this.visibility = View.VISIBLE
    this.alpha = 0f

    this.animate().apply {
        duration = animDuration
        alpha(1f)
    }.withEndAction {
        after()
    }
}

fun TextView.blockToBlock(
    text: String,
    before: () -> Unit = {},
    after: () -> Unit = {},
    speed: Long = 70L,
    afterDelay: Long = 0L
) = CoroutineScope(Dispatchers.Main).launch {
    this@blockToBlock.text = ""

    before()

    text.forEach {
        delay(speed)
        if (!this@blockToBlock.isVisible) return@launch

        val placeHolder = "${this@blockToBlock.text}$it"
        this@blockToBlock.text = placeHolder
    }

    delay(afterDelay)
    after()
}

fun View.disappear(
    before: () -> Unit = {},
    after: () -> Unit = {},
    animDuration: Long = 300L
) {
    before()
    this.animate().apply {
        duration = animDuration
        alpha(0f)
    }.withEndAction {
        after()
        this.visibility = View.INVISIBLE
    }
}

fun View.setOnCustomClickListener(unit: () -> Unit) {
    this.setOnClickListener {
        this.pressAnimated()
        unit()
    }
}

fun View.pressAnimated(unit: () -> Unit = {}) {
    this.animate().apply {
        duration = 150
        alpha(0f)
    }.withEndAction {
        this.animate().apply {
            duration = 150
            alpha(1f)
        }.withEndAction(unit)
    }
}

/*
* listOf(
        CategoryBlock(
            name = "Бакалея",
            categories = listOf(
                Category(name = "Консервы", imageHref = ""),
                Category(name = "Макароны, крупы и мука", imageHref = ""),
                Category(name = "Масло, соусы и приправы", imageHref = ""),
                Category(name = "Мюсли, завтраки", imageHref = ""),
                Category(name = "Чай и кофе", imageHref = "")
            )
        ),
        CategoryBlock(
            name = "Вкусняшки",
            categories = listOf(
                Category(name = "Варенье и мёд", imageHref = ""),
                Category(name = "Вафли и печенье", imageHref = ""),
                Category(name = "Жвачка, драже и мармелад", imageHref = ""),
                Category(name = "Мороженое", imageHref = ""),
                Category(name = "Шоколад и конфеты", imageHref = "")
            )
        ),
        CategoryBlock(
            name = "Вода и напитки",
            categories = listOf(
                Category(name = "Вода", imageHref = ""),
                Category(name = "Газировка и лимонады", imageHref = ""),
                Category(name = "Соки и морсы", imageHref = ""),
                Category(name = "Холодный чай и квас", imageHref = ""),
                Category(name = "Энергетики", imageHref = "")
            )
        ),
        CategoryBlock(
            name = "Для детей и родителей",
            categories = listOf(
                Category(name = "Детское питание", imageHref = ""),
                Category(name = "Развлечения", imageHref = ""),
                Category(name = "Уход за детьми", imageHref = "")
            )
        ),
        CategoryBlock(
            name = "Для дома",
            categories = listOf(
                Category(name = "Кухня", imageHref = ""),
                Category(name = "Полезные мелочи", imageHref = ""),
                Category(name = "Стирка", imageHref = ""),
                Category(name = "Уборка", imageHref = "")
            )
        ),
        CategoryBlock(
            name = "Для животных",
            categories = listOf(
                Category(name = "Для кошек", imageHref = ""),
                Category(name = "Для собак", imageHref = "")
            )
        ),
        CategoryBlock(
            name = "Заморозка",
            categories = listOf(
                Category(name = "Овощи и фрукты", imageHref = ""),
                Category(name = "Пельмени и вареники", imageHref = ""),
                Category(name = "Раз-два и готово", imageHref = ""),
                Category(name = "Рыба и дары моря", imageHref = "")
            )
        ),
        CategoryBlock(
            name = "Молоко, яйца, сыр",
            categories = listOf(
                Category(name = "Йогурты и десерты", imageHref = ""),
                Category(name = "Молочное и яйца", imageHref = ""),
                Category(name = "Сыр", imageHref = "")
            )
        ),
        CategoryBlock(
            name = "Мясо и рыба",
            categories = listOf(
                Category(name = "Колбаса и сосиски", imageHref = ""),
                Category(name = "Мясо и птица", imageHref = ""),
                Category(name = "Рыба и дары моря", imageHref = "")
            )
        ),
        CategoryBlock(
            name = "Овощи и фрукты",
            categories = listOf(
                Category(name = "Овощи и зелень", imageHref = ""),
                Category(name = "Фрукты и ягоды", imageHref = "")
            )
        ),
        CategoryBlock(
            name = "Снеки",
            categories = listOf(
                Category(name = "Семечки, орехи и сухофрукты", imageHref = ""),
                Category(name = "Чипсы, снеки и сухарики", imageHref = "")
            )
        ),
        CategoryBlock(
            name = "Уход и личная гигиена",
            categories = listOf(
                Category(name = "Бумага и салфетки", imageHref = ""),
                Category(name = "Для волос", imageHref = ""),
                Category(name = "Для зубов и полости рта", imageHref = ""),
                Category(name = "Личная гигиена", imageHref = ""),
                Category(name = "Уход и макияж", imageHref = "")
            )
        ),
        CategoryBlock(
            name = "Хлебобулочные изделия",
            categories = listOf(
                Category(name = "Выпечка", imageHref = ""),
                Category(name = "Хлеб", imageHref = ""),
                Category(name = "Хлебцы, сухари и сушки", imageHref = "")
            )
        )
    )
* */