package uk.gov.hmcts.reform.em.annotation.cftlib;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.rse.ccd.lib.api.CFTLib;
import uk.gov.hmcts.rse.ccd.lib.api.CFTLibConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class CftLibConfig implements CFTLibConfigurer {

    @Override
    public void configure(CFTLib lib) throws Exception {

        lib.createRoles("caseworker","caseworker-publiclaw");

        lib.importDefinition(Files.readAllBytes(
                Path.of("src/aat/resources/adv_annotation_functional_tests_ccd_def.xlsx")));

    }
}
