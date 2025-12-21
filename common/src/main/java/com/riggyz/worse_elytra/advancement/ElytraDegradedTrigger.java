package com.riggyz.worse_elytra.advancement;

import com.google.gson.JsonObject;
import com.riggyz.worse_elytra.Constants;
import com.riggyz.worse_elytra.elytra.StateHandler.ElytraState;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Custom trigger that allows for data driven advancements. Is used for both the
 * partial and fully broken advancements.
 */
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

    /**
     * External trigger function so that the mod has control over when to trigger an
     * advancement. Can be accomplished though vanilla api but this is helpful for
     * custom logic.
     * 
     * @param player player that triggered the advancement
     * @param state  the elytra state to check for a match
     */
    public void trigger(ServerPlayer player, ElytraState state) {
        this.trigger(player, instance -> instance.matches(state));
    }

    /**
     * Custom trigger logic that ties into the advancement JSON. Allows for a
     * definition of a broken elytra trigger on any state or any state change.
     */
    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ElytraState targetState;

        /**
         * Constructor, used to instantiate the class.
         * 
         * @param predicate   some context predicate needed by minecraft
         * @param targetState the state to target for the advancement
         */
        public TriggerInstance(ContextAwarePredicate predicate, ElytraState targetState) {
            super(ID, predicate);
            this.targetState = targetState;
        }

        /**
         * Custom matching logic. If a state is defined then the event state must match.
         * If no state is defined then assume the trigger procs on ANY elytra state
         * change.
         * 
         * @param degradedToState the state that triggered the check
         * 
         * @return whether the criteria matches
         */
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