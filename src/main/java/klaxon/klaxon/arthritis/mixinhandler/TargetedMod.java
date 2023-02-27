package klaxon.klaxon.arthritis.mixinhandler;

public enum TargetedMod {

    // MC has no modid or coremodclass to look for, it's always there
    VANILLA("Minecraft", null, null),

    // Name in @Mod
    public final String modName;
    // Class that implements IFMLLoadingPlugin
    public final String coreModClass;
    // Modid in @Mod
    public final String modId;

    TargetedMod(String modName, String coreModClass, String modId) {
        this.modName = modName;
        this.coreModClass = coreModClass;
        this.modId = modId;
    }

    @Override
    public String toString() {
        return "TargetedMod{modName='" + modName + "', coreModClass='" + coreModClass + "', modId='" + modId + "'}";
    }
}
