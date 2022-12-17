# 贡献指南

## 代码风格

基于 JetBrains 的 [Kotlin 风格指南](https://kotlinlang.org/docs/coding-conventions.html#names-for-test-methods)，但仍有少许修改。

- 列宽
    - 修改 `Hard wrap` 为 160 字符。
- 语言
    - 如无特殊情况，注释应当使用中文编写。
    - 如项目内存在中文文本（注释、代码文档等），其文案排版参考 [中文文案排版指北](https://github.com/sparanoid/chinese-copywriting-guidelines)。
- 空行
    - 在每个文件（类或接口等）中的第一个成员前和最后一个成员后放置一个空行。
    - 在 `when` 表达式中，多行输入下中间用来隔断的新行可以省略。
    - **不要写的太挤，也不要空太多行。**
- 异常
    - 可合理标注 `Suppress` 注解以避免 IDEA 的异常检查，例如在 IDEA
      不认识的单词上面标注 `@Suppress("SpellCheckingInspection")`。
    - 对于被忽略的异常，需将异常变量命名为 `ignored`。
- 测试
    - 请确保新增代码在本地测试运行正常后再进行提交。

为了帮助您快速上手，这里是一段格式正确的范例代码。

```kotlin
package net.geekmc.turingcore.example

import net.geekmc.turingcore.util.globalEvent
import net.geekmc.turingcore.util.info
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent
import world.cepi.kstom.event.listenOnly

/**
 * 玩家档案，用于提供最基本的数据管理
 */
class Profile(val player: Player) {

    // 玩家货币，可用于兑换非常牛逼的物品
    var coins = 0

    companion object {

        private val profiles = mutableMapOf<String, Profile>()

        // 初始化注册事件
        fun init() {
            globalEvent.listenOnly<PlayerLoginEvent> {
                profiles[player.username] = Profile(player).apply {
                    read()
                }
                val userName = player.username
                when (player.latency) {
                    in 0..50 -> {
                        info("一个网络良好的玩家 ($userName) 进入了游戏。")
                        player.sendMessage("欢迎您进入测试服！")
                    }
                    in 51..150 -> {
                        info("一个网络不怎么好的玩家 ($userName) 进入了游戏。")
                        player.sendMessage("你进入了测试服。")
                    }
                    else -> {
                        info("一个大卡逼 ($userName) 进入了服务器，系统判定他充不起钱，将其踢出。")
                        player.kick("滚。")
                    }
                }
            }
            globalEvent.listenOnly<PlayerDisconnectEvent> {
                profiles.remove(player.username)?.apply {
                    save()
                }
            }
        }
    }

    // 从数据库里读取信息
    fun read() {}

    // 将信息保存至数据库
    fun save() {}
}
```

## 推送代码

我们参考前端框架 `Angular`
的 [提交信息规范](https://docs.google.com/document/d/1QrDFcIiPjSLDn3EL15IJygNPiHORgU1_OOAqWjiDU5Y/edit#)。

建议搭配使用 IDEA 插件 [git-commit-template](https://plugins.jetbrains.com/plugin/9861-git-commit-template) 以快速上手。

### 消息格式

```
<类型>(<作用域>): <主题>
<空行>
<正文>
<空行>
<结尾>
```

信息使用中文，每次提交的信息不应超过 **100** 个字符。

**表头必写**，但表头里的作用域可以选择填写。 例：

```
docs(README.md): update README
```

```
docs: update README
```

这两个是等价的，是否添加作用域取决于你。

### 消息类型

- **build**: 构建及相关变动。
- **docs**: 文档更改。
- **feat**: 添加新特性。
- **fix**: 修复 BUG。
- **perf**: 性能提高更改。
- **refactor**: 重构。
- **style**: 不影响代码的更改 (空格或格式化等)。
- **test**: 添加测试。

## 版本格式

详见 [Semver 规范](https://semver.org/lang/zh-CN/)。