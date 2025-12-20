package com.riggyz.worse_elytra.advancement;

import com.google.gson.JsonObject;
import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.elytra.ElytraStateHandler.ElytraState;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ElytraDegradedTrigger extends SimpleCriterionTrigger<ElytraDegradedTrigger.TriggerInstance> {

    private static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "elytra_degraded");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject json, ContextAwarePredicate predicate,
            DeserializationContext context) {
        ElytraState targetState = null;
        if (json.has("state")) {
            String stateName = json.get("state").getAsString();
            targetState = ElytraState.valueOf(stateName.toUpperCase());
        }
        return new TriggerInstance(predicate, targetState);
    }

    public void trigger(ServerPlayer player, ElytraState newState) {
        this.trigger(player, instance -> instance.matches(newState));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ElytraState targetState;

        public TriggerInstance(ContextAwarePredicate predicate, ElytraState targetState) {
            super(ID, predicate);
            this.targetState = targetState;
        }

        public boolean matches(ElytraState degradedToState) {
            if (this.targetState == null) {
                return true; // Any degradation counts
            }
            return this.targetState == degradedToState;
        }

        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            if (this.targetState != null) {
                json.addProperty("state", this.targetState.name().toLowerCase());
            }
            return json;
        }
    }
}