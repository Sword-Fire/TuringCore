package net.geekmc.turingcore.util.lang

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import net.geekmc.turingcore.readResourceAsString
import kotlin.io.path.writeText

class LanguageTest : StringSpec({
    val textPath = tempfile("turing-core-lang-text-message-test").toPath()
    val actionPath = tempfile("turing-core-lang-actionbar-message-test").toPath()
    val titlePath = tempfile("turing-core-lang-title-message-test").toPath()

    val textString = readResourceAsString("/lang/text-message-test.yml")
    val actionString = readResourceAsString("/lang/actionbar-message-test.yml")
    val titleString = readResourceAsString("/lang/title-message-test.yml")

    beforeSpec {
        textPath.writeText(textString)
        actionPath.writeText(actionString)
        titlePath.writeText(titleString)
    }

    "parse text message from file should works" {
        LanguageParser.parseLangYaml(textPath)["text-message"] shouldBe MessageText("test message")
    }

    "parse text message from string should works" {
        LanguageParser.parseLangYaml(textString)["text-message"] shouldBe MessageText("test message")
    }

    "parse actionbar message from file should works" {
        LanguageParser.parseLangYaml(actionPath)["actionbar-message"] shouldBe MessageActionBar("test message", 10)
    }

    "parse actionbar message from string should works" {
        LanguageParser.parseLangYaml(actionString)["actionbar-message"] shouldBe MessageActionBar("test message", 10)
    }

    "parse title message from file should works" {
        LanguageParser.parseLangYaml(titlePath)["title-message"] shouldBe MessageTitle(
            "test title",
            "test subtitle",
            10,
            20,
            30
        )
    }

    "parse title message from string should works" {
        LanguageParser.parseLangYaml(titleString)["title-message"] shouldBe MessageTitle(
            "test title",
            "test subtitle",
            10,
            20,
            30
        )
    }
})