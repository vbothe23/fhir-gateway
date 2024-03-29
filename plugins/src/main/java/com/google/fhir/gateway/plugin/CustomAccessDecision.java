package com.google.fhir.gateway.plugin;

import com.google.fhir.gateway.interfaces.AccessDecision;
import com.google.fhir.gateway.interfaces.RequestDetailsReader;
import com.google.fhir.gateway.interfaces.RequestMutation;
import org.apache.http.HttpResponse;
import org.hl7.fhir.r4.model.ResourceType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class CustomAccessDecision implements AccessDecision {

    private static final Logger logger = LoggerFactory.getLogger(AccessDecision.class);

    private final boolean accessGranted;

    private CustomAccessDecision(boolean accessGranted) {
        this.accessGranted = accessGranted;
    }

    @Override
    public boolean canAccess() {
        return accessGranted;
    }

    @Nullable
    @Override
    public RequestMutation getRequestMutation(RequestDetailsReader requestDetailsReader) {
        if (ResourceType.Patient.name().equals(requestDetailsReader.getRequestPath())) {
            HashMap<String, List<String>> paramMutations = new HashMap<>();
            paramMutations.put(
                    "_elements",
                    new ArrayList<>(Arrays.asList("id.name,birthDate"))
            );
            return RequestMutation.builder().queryParams(paramMutations).build();
        }
        return null;
    }

    @Override
    public String postProcess(RequestDetailsReader request, HttpResponse response) throws IOException {
        return null;
    }

    public static AccessDecision accessGranted() {
        return new CustomAccessDecision(true);
    }

    public static AccessDecision accessDenied() {
        return new CustomAccessDecision(false);
    }
}
