package yahvya.implementation.graphical.navigation

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import yahvya.implementation.configurations.ApplicationConfig
import yahvya.implementation.configurations.PathsConfig

/**
 * @brief application pages navigation manager
 */
open class NavigationManager(
    /**
     * @brief application main stage
     */
    var mainStage: Stage,
    /**
     * @brief application interface configurator
     */
    val interfaceConfigurator: InterfaceConfigurator
){
    /**
     * @brief pages storage
     */
    protected val pagesStore: HashMap<String, SceneDatas> = HashMap()

    /**
     * @brief switch page
     * @param fxmlPath path of the fxml file after the 'PathConfig.pagesBasePath'
     * @param datas datas to provide to the controller or null if no datas
     * @return success state
     */
    fun switchOnController(fxmlPath: String,datas: Any? = null):Boolean{
        ApplicationConfig.LOGGER.info("Switching controller to $fxmlPath")

        try{
            // load the scene datas
            lateinit var sceneDatas: SceneDatas

            if(!this.pagesStore.containsKey(fxmlPath)){
                val fxmlLoader = FXMLLoader(ApplicationConfig.ROOT_CLASS.getResource("${PathsConfig.PAGES_DIRECTORY_PATH}$fxmlPath"))

                sceneDatas = SceneDatas(
                    fxmlLoader= fxmlLoader,
                    loadedScene= Scene(fxmlLoader.load())
                )
            }
            else
                sceneDatas = this.pagesStore[fxmlPath]!!

            // load and call controller configuration methods

            val sceneController: AController = sceneDatas.fxmlLoader.getController<AController>()
            sceneController.receiveDatas(datas= datas, navigationManager= this)

            if(sceneController.storeCurrentVersionOnSwitch())
                this.pagesStore.put(fxmlPath, sceneDatas)
            else
                this.pagesStore.remove(fxmlPath)

            // configure the interface and show the controller

            this.mainStage.scene = sceneDatas.loadedScene
            this.interfaceConfigurator.configureInterface(stage= this.mainStage)

            if(sceneController.showAfter() && !this.mainStage.isShowing)
                this.mainStage.show()

            ApplicationConfig.LOGGER.info("Switched to $fxmlPath")

            return true
        }
        catch(_: Exception){
            ApplicationConfig.LOGGER.error("Fail to switch to the controller")
            return false
        }
    }

    /**
     * @brief scene stored datas
     */
    protected data class SceneDatas(
        /**
         * @brief linked fxml loader
         */
        val fxmlLoader: FXMLLoader,
        /**
         * @brief loaded parent
         */
        val loadedScene: Scene
    )
}