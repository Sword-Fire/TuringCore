package net.geekmc.turingcore.skin

import kotlinx.coroutines.*
import net.geekmc.turingcore.TuringCore
import net.geekmc.turingcore.data.yaml.YamlData
import net.geekmc.turingcore.event.EventNodes
import net.geekmc.turingcore.service.Service
import net.geekmc.turingcore.util.coroutine.MinestomAsync
import net.geekmc.turingcore.util.resolvePath
import net.geekmc.turingcore.util.saveResource
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.player.PlayerSkinInitEvent
import world.cepi.kstom.event.listenOnly

/**
 * 基于玩家名的皮肤服务。
 */
// TODO 处理连不上mojang的情况
object SkinService : Service() {

    private const val PATH = "data/skin.yml"

    private lateinit var skinData: YamlData
    private lateinit var scope: CoroutineScope

    override fun onEnable() {
        scope = CoroutineScope(Dispatchers.MinestomAsync)
        TuringCore.INSTANCE.saveResource(PATH)
        // 加载皮肤数据。
        skinData = YamlData(TuringCore.INSTANCE.resolvePath(PATH), SkinService::class.java)
        EventNodes.DEFAULT.listenOnly<PlayerSkinInitEvent> {
            // 根据玩家的用户名来获取相应皮肤。
            val bean = skinData.getOrNull<PlayerSkinBean>(player.username)
            scope.launch {
                if (bean != null) {
                    player.skin = bean.toPlayerSkin()
                }
                val skin = withContext(Dispatchers.IO) {
                    PlayerSkin.fromUsername(player.username)
                } ?: return@launch
                // 获取的皮肤是新皮肤，则更新，若缓存不存在也会执行更新操作。
                // FIXME: 每次获取的材质有一小段字段不一样，所以必定更新，暂时无法解决。
                if (bean == null || bean.toPlayerSkin().textures() != skin.textures()) {
                    skinData[player.username] = skin.toBean()
                    player.skin = skin
                }
            }
        }
    }

    override fun onDisable() {
        scope.cancel()
        skinData.save()
    }

    private data class PlayerSkinBean(var textures: String = "", var signature: String = "") {
        fun toPlayerSkin(): PlayerSkin {
            return PlayerSkin(textures, signature)
        }
    }

    private fun PlayerSkin.toBean(): PlayerSkinBean {
        return PlayerSkinBean(textures(), signature())
    }
}