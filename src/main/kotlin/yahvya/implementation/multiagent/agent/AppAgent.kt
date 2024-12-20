package yahvya.implementation.multiagent.agent

import jade.core.Agent
import yahvya.implementation.configurations.ApplicationConfig
import yahvya.implementation.multiagent.definitions.Box
import yahvya.implementation.multiagent.definitions.Exportable
import yahvya.implementation.multiagent.simulation.Simulation
import yahvya.implementation.multiagent.simulation.SimulationConfiguration

/**
 * @brief application agent
 */
class AppAgent : Agent(), Exportable{
    /**
     * @brief agent box
     */
    lateinit var box: Box

    /**
     * @brief agent current behaviours
     */
    val behaviours: MutableList<AppAgentBehaviour> = mutableListOf()

    /**
     * @brief parent simulation
     */
    lateinit var parentSimulation: Simulation

    /**
     * @brief remove a behaviour to the agent
     * @return this
     * @attention call this method instead of "removeBehaviour"
     */
    fun removeAppAgentBehaviour(behaviour: AppAgentBehaviour): AppAgent{
        this.removeBehaviour(behaviour)
        this.behaviours.remove(behaviour)

        return this
    }

    /**
     * @brief add a behaviour to the agent
     * @return this
     * @attention call this method instead of "addBehaviour"
     */
    fun addAppAgentBehaviour(behaviour: AppAgentBehaviour): AppAgent{
        this.addBehaviour(behaviour)
        this.behaviours.add(behaviour)

        return this
    }

    override fun setup() {
        if(
            this.arguments.size < 2 ||
            this.arguments[0] !is Simulation ||
            this.arguments[1] !is SimulationConfiguration.AgentInitialConfig
        )
            return

        this.parentSimulation = this.arguments[0] as Simulation
        val defaultConfig = this.arguments[1] as SimulationConfiguration.AgentInitialConfig

        this.box = defaultConfig.box

        defaultConfig.behaviours.forEach{ defaultBehaviour -> this.addAppAgentBehaviour(behaviour = defaultBehaviour)}
    }

    override fun loadFromExportedConfig(configuration: Map<*, *>): Boolean {
        try{
            ApplicationConfig.LOGGER.info("Loading agent from exported configuration")

            this.box = Box.createFromConfiguration(configuration = configuration[ExportKeys.BOX] as Map<*,*>)

            (configuration[ExportKeys.BEHAVIOURS] as List<*>).forEach { config ->
                val behaviourConfig = config as Map<*,*>

                this.addAppAgentBehaviour(behaviour = AppAgentBehaviour.createFromConfiguration(configuration = behaviourConfig))
            }

            return true
        }
        catch(e: Exception){
            ApplicationConfig.LOGGER.error("Fail to load agent from configuration due to <${e.message}>")
            return false
        }
    }

    override fun exportConfig(): Map<*, *> = mapOf(
        ExportKeys.BOX to this.box.exportConfig(),
        ExportKeys.BEHAVIOURS to this.behaviours.map{ it.exportConfig() }
    )

    /**
     * @brief export keys
     */
    data object ExportKeys{
        /**
         * @brief agent box
         */
        const val BOX = "box"

        /**
         * @brief agent behaviours
         */
        const val BEHAVIOURS = "behaviours"
    }
}