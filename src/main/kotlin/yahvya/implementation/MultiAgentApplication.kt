package yahvya.implementation

import javafx.application.Application
import javafx.stage.Stage
import mu.KotlinLogging
import yahvya.implementation.configurations.ApplicationConfig
import yahvya.implementation.configurations.ScreensConfig
import yahvya.implementation.graphical.navigation.DefaultInterfaceConfigurator
import yahvya.implementation.graphical.navigation.InterfaceConfigurator
import yahvya.implementation.graphical.navigation.NavigationManager

/**
 * @brief application
 */
open class MultiAgentApplication : Application(), InterfaceConfigurator by DefaultInterfaceConfigurator() {
    override fun start(stage: Stage?) {
        if(stage == null) {
            println("Fail to launch app")
            return
        }

        this.configureApplication(mainStage = stage)
        ApplicationConfig.NAVIGATION_MANAGER.switchOnController(fxmlPath = ScreensConfig.WELCOME_SCREEN)
    }

    /**
     * @brief configure the application
     * @param mainStage application main Stage
     * @return this
     * @throws Nothing
     */
    protected fun configureApplication(mainStage: Stage): MultiAgentApplication{
        ApplicationConfig.init(
            rootClass = javaClass,
            navigationManager = NavigationManager(
                mainStage = mainStage,
                interfaceConfigurator = this
            ),
            pluginsParentDirectory = "plugins",
            logger =  KotlinLogging.logger {},
            applicationName = "SimuGand",
            authorGithubLink = "https://github.com/yahvya"
        )

        return this
    }
}

fun main() = Application.launch(MultiAgentApplication::class.java)