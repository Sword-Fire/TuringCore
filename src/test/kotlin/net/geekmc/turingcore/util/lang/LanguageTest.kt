package net.geekmc.turingcore.util.lang

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import kotlin.io.path.writeText

class LanguageTest : FunSpec({
    val textFile = tempfile("turing-core-lang-text-message-test").toPath()
    val actionFile = tempfile("turing-core-lang-actionbar-message-test").toPath()
    val titleFile = tempfile("turing-core-lang-title-message-test").toPath()

    beforeSpec {
        textFile.writeText(
            """
            |text-message: test message
            """.trimMargin()
        )
        actionFile.writeText(
            """
            |actionbar-message:
            |  type: actionbar
            |  msg: test message
            |  duration: 10
            """.trimMargin()
        )
        titleFile.writeText(
            """
            |title-message:
            |  type: title
            |  title: test title
            |  subtitle: test subtitle
            |  fadein: 10
            |  duration: 20
            |  fadeout: 30
            """.trimMargin()
        )
    }

    test("parse text message should works") {
        LanguageService.parseLangYaml(textFile)["text-message"] shouldBe MessageText("test message")
    }

    test("parse actionbar message should works") {
        LanguageService.parseLangYaml(actionFile)["actionbar-message"] shouldBe MessageActionBar("test message", 10)
    }

    test("parse title message should works") {
        LanguageService.parseLangYaml(titleFile)["title-message"] shouldBe MessageTitle(
            "test title",
            "test subtitle",
            10,
            20,
            30
        )
    }
})