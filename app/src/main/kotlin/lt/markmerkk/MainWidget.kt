package lt.markmerkk

import com.google.inject.Guice
import javafx.scene.Parent
import javafx.scene.control.Button
import lt.markmerkk.changelog_gen.credits.CreditsWidget
import org.slf4j.LoggerFactory
import tornadofx.*
import kotlin.reflect.KClass

class MainWidget : View("Change log generator (${BuildConfig.VERSION})") {

    private val guice = Guice.createInjector(AppModule())

    init {
        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>) = guice.getInstance(type.java)
        }
    }

    override val root: Parent = borderpane {
        top {
            menubar {
                menu("About") {
                    item("Credits") {
                        setOnAction {
                            val creditsWidget = find<CreditsWidget>()
                            creditsWidget.openModal(block = true)
                        }
                    }
                }
            }
        }
        center {
            label("Hello world")
        }
    }

    override fun onDock() {
    }

    override fun onUndock() {}

    companion object {
        val logger = LoggerFactory.getLogger(MainWidget::class.java)!!
    }

}