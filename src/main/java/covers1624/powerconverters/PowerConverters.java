package covers1624.powerconverters;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import covers1624.powerconverters.grid.GridTickHandler;
import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.handler.PCEventHandler;
import covers1624.powerconverters.handler.PCGUIHandler;
import covers1624.powerconverters.init.ModBlocks;
import covers1624.powerconverters.init.ModItems;
import covers1624.powerconverters.init.PowerSystems;
import covers1624.powerconverters.init.Recipes;
import covers1624.powerconverters.init.WorldGenerators;
import covers1624.powerconverters.proxy.IPCProxy;
import covers1624.powerconverters.reference.Reference;
import covers1624.powerconverters.updatechecker.UpdateManager;
import covers1624.powerconverters.util.LogHelper;
import covers1624.powerconverters.util.RFHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = "after:BuildCraft|Energy;after:factorization;after:IC2;after:Railcraft;after:ThermalExpansion", guiFactory = Reference.GUI_FACTORY)
public class PowerConverters {

	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
	public static IPCProxy proxy;

	@Instance("Power Converters")
	public static PowerConverters instance;

	private static ConfigurationHandler configHandler;

	public static int steamId = -1;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		instance = this;

		LogHelper.info("Power Converters PreInitialization Started.");

		LogHelper.trace("Checking For RF API...");
		RFHelper.init();

		LogHelper.trace("Initializing Configuration File");
		configHandler = new ConfigurationHandler(event.getSuggestedConfigurationFile());

		LogHelper.trace("Registering Update Manager");
		UpdateManager updateManager = new UpdateManager();
		FMLCommonHandler.instance().bus().register(updateManager);

		LogHelper.trace("Registering Event Handlers.");
		PCEventHandler eventHandler = new PCEventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		FMLCommonHandler.instance().bus().register(eventHandler);

		LogHelper.trace("Initializing PowerSystems");
		PowerSystems.init();

		LogHelper.trace("Initializing ChargeHandlers");
		PowerSystems.initChargeHandlers();

		LogHelper.info("Power Converters PreInitialization Finished.");
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		LogHelper.info("Power Converters Core Initialization Started.");

		MinecraftForge.EVENT_BUS.register(GridTickHandler.energy);
		FMLCommonHandler.instance().bus().register(GridTickHandler.energy);

		LogHelper.trace("Registering Gui Handler.");
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new PCGUIHandler());

		LogHelper.trace("Registering Blocks.");
		ModBlocks.init();

		LogHelper.trace("Registering Items.");
		ModItems.init();

		LogHelper.trace("Registering World Generators.");
		WorldGenerators.init();

		LogHelper.trace("Registering Client Rendering.");
		proxy.initRendering();

		if (ConfigurationHandler.useTechRebornRecipes) {

		} else if (ConfigurationHandler.useThermalExpansionRecipes) {

		} else {
			LogHelper.trace("Registering Default Recipes.");
			Recipes.initDefaults();
		}

		if (Loader.isModLoaded("Waila")) {
			FMLInterModComms.sendMessage("Waila", "register", "covers1624.powerconverters.waila.WailaModule.callBackRegister");
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (FluidRegistry.isFluidRegistered("steam")) {
			steamId = FluidRegistry.getFluidID("steam");
		}
	}
}