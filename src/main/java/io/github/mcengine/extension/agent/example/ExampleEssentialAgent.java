package io.github.mcengine.extension.agent.example;

import io.github.mcengine.api.core.MCEngineCoreApi;
import io.github.mcengine.api.core.extension.logger.MCEngineExtensionLogger;
import io.github.mcengine.api.essential.extension.api.IMCEngineEssentialAPI;

import io.github.mcengine.extension.agent.example.command.EssentialAgentCommand;
import io.github.mcengine.extension.agent.example.listener.EssentialAgentListener;
import io.github.mcengine.extension.agent.example.tabcompleter.EssentialAgentTabCompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Main class for the Essential <b>Agent</b> example module.
 * <p>
 * Registers the {@code /essentialagentexample} command and related event listeners.
 * <p>
 * Note: This class name and packages were migrated from "API" to "Agent" while preserving the
 * original {@link IMCEngineEssentialAPI} integration for compatibility.
 */
public class ExampleEssentialAgent implements IMCEngineEssentialAPI {

    /** Custom extension logger for this module, with contextual labeling. */
    private MCEngineExtensionLogger logger;

    /**
     * Initializes the Essential Agent example module.
     * Called automatically by the MCEngine core plugin.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onLoad(Plugin plugin) {
        // Initialize contextual logger once and keep it for later use.
        this.logger = new MCEngineExtensionLogger(plugin, "Agent", "EssentialExampleAgent");

        try {
            // Register event listener
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents(new EssentialAgentListener(plugin, this.logger), plugin);

            // Reflectively access Bukkit's CommandMap
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            // Define the /essentialagentexample command
            Command essentialAgentExampleCommand = new Command("essentialagentexample") {

                /** Handles command execution for /essentialagentexample. */
                private final EssentialAgentCommand handler = new EssentialAgentCommand();

                /** Handles tab-completion for /essentialagentexample. */
                private final EssentialAgentTabCompleter completer = new EssentialAgentTabCompleter();

                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    return handler.onCommand(sender, this, label, args);
                }

                @Override
                public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                    return completer.onTabComplete(sender, this, alias, args);
                }
            };

            essentialAgentExampleCommand.setDescription("Essential Agent example command.");
            essentialAgentExampleCommand.setUsage("/essentialagentexample");

            // Dynamically register the /essentialagentexample command
            commandMap.register(plugin.getName().toLowerCase(), essentialAgentExampleCommand);

            this.logger.info("Enabled successfully.");
        } catch (Exception e) {
            this.logger.warning("Failed to initialize ExampleEssentialAgent: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the Essential Agent example module is disabled/unloaded.
     * No explicit unregistration is required for the anonymous Command here.
     *
     * @param plugin The Bukkit plugin instance.
     */
    @Override
    public void onDisload(Plugin plugin) {
        if (this.logger != null) {
            this.logger.info("Disabled.");
        }
    }

    /**
     * Sets the unique ID for this module.
     *
     * @param id the assigned identifier (ignored; a fixed ID is used for consistency)
     */
    @Override
    public void setId(String id) {
        MCEngineCoreApi.setId("mcengine-essential-agent-example");
    }
}
