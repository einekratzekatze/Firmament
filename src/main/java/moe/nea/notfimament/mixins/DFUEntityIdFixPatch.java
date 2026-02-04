

package moe.nea.notfimament.mixins;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.fixes.EntityIdFix;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

// TODO: rework this
@Mixin(EntityIdFix.class)
public abstract class DFUEntityIdFixPatch extends DataFix {
    @Shadow
    @Final
    private static Map<String, String> ID_MAP;

    public DFUEntityIdFixPatch(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Inject(method = "makeRule", at = @At("RETURN"), cancellable = true)
    public void onMakeRule(CallbackInfoReturnable<TypeRewriteRule> cir) {
        cir.setReturnValue(TypeRewriteRule.seq(fixTypeEverywhere("EntityIdFix", getInputSchema().findChoiceType(References.ENTITY), getOutputSchema().findChoiceType(References.ENTITY), dynamicOps -> pair -> ((Pair) pair).mapFirst(string -> ID_MAP.getOrDefault(string, (String) string))), convertUnchecked("Fix Type", getInputSchema().getType(References.ITEM_STACK), getOutputSchema().getType(References.ITEM_STACK))));
    }
}
