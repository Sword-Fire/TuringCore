package net.geekmc.turingcore.util.lang

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import net.geekmc.turingcore.readResourceAsString
import kotlin.io.path.writeText

/**
 * ```
 * msg-example-1: "chat message"
 * msg-example-2:
 *   type: "title"
 *   main: "message in main title"
 *   sub: "message in sub title"
 *   // 单位为tick。如果不填，会自动根据消息长度计算时间。不会低于1.5s。
 *   fadein: 20
 *   duration: 100
 *   fadeout: 20
 * msg-example-3:
 *   type: "actionbar"
 *   msg: "actionbar message"
 *   // 单位为tick。如果不填，会自动根据消息长度计算时间。不会低于1.5s。
 *   duration: 100
 * msg-example-4: null
 * msg-example-5:
 *   type: "multi"
 *   msg: |
 *     chat message1
 *     chat message2
 *   interval: 20 // (单位tick)
 *  ```
 */
class LanguageTest : StringSpec({
    val textPath = tempfile("turing-core-lang-text-message-test").toPath()
    val titlePath = tempfile("turing-core-lang-title-message-test").toPath()
    val actionPath = tempfile("turing-core-lang-actionbar-message-test").toPath()
    val nullPath = tempfile("turing-core-lang-null-message-test").toPath()
    val multiPath = tempfile("turing-core-lang-multi-message-test").toPath()

    val textString = readResourceAsString("/lang/text-message-test.yml")
    val titleString = readResourceAsString("/lang/title-message-test.yml")
    val actionString = readResourceAsString("/lang/actionbar-message-test.yml")
    val nullString = readResourceAsString("/lang/null-message-test.yml")
    val multiString = readResourceAsString("/lang/multi-message-test.yml")

    beforeSpec {
        textPath.writeText(textString)
        titlePath.writeText(titleString)
        actionPath.writeText(actionString)
        nullPath.writeText(nullString)
        multiPath.writeText(multiString)
    }

    "parse text message from file should works" {
        LanguageParser.parseLangYaml(textPath)["text-message"] shouldBe MessageText("test message")
    }

    "parse text message from string should works" {
        LanguageParser.parseLangYaml(textString)["text-message"] shouldBe MessageText("test message")
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

    "parse actionbar message from file should works" {
        LanguageParser.parseLangYaml(actionPath)["actionbar-message"] shouldBe MessageActionBar("test message", 10)
    }

    "parse actionbar message from string should works" {
        LanguageParser.parseLangYaml(actionString)["actionbar-message"] shouldBe MessageActionBar("test message", 10)
    }

    "parse null message from file should works" {
        LanguageParser.parseLangYaml(nullPath)["null-message"] shouldBe MessageNull()
    }

    "parse null message from string should works" {
        LanguageParser.parseLangYaml(nullString)["null-message"] shouldBe MessageNull()
    }

    "parse multi message from file should works" {
        LanguageParser.parseLangYaml(multiPath)["multi-message"] shouldBe MessageMulti(
            listOf(
                "Hello,",
                "World!"
            ),
            20
        )
    }

    "parse multi message from string should works" {
        LanguageParser.parseLangYaml(multiString)["multi-message"] shouldBe MessageMulti(
            listOf(
                "Hello,",
                "World!"
            ),
            20
        )
    }
})