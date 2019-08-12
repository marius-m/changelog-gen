package lt.markmerkk

import javafx.application.Application
import tornadofx.App

class Main : App(MainWidget::class) {

    override fun start(stage: javafx.stage.Stage) {
        super.start(stage)
        stage.scene.stylesheets.add("org/kordamp/bootstrapfx/bootstrapfx.css")
        stage.minWidth = MIN_WIDTH
        stage.minHeight = MIN_HEIGHT
        stage.width = MIN_WIDTH
        stage.height = MIN_HEIGHT
    }

    override fun stop() {
        super.stop()
    }

    companion object {
        const val MIN_WIDTH = 600.0
        const val MIN_HEIGHT = 400.0
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java)
}
