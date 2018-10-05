package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.IdamDetailsRepository;
import uk.gov.hmcts.reform.em.annotation.service.IdamDetailsFilterService;

@Service
@Transactional
public class IdamDetailsFilterServiceImpl implements IdamDetailsFilterService {

    private final IdamDetailsRepository idamDetailsRepository;

    public IdamDetailsFilterServiceImpl(IdamDetailsRepository idamDetailsRepository) {
        this.idamDetailsRepository = idamDetailsRepository;
    }

    @Override
    public void saveIdamDetails() {
        SecurityUtils.getCurrentUserDetails().ifPresent( emServiceAndUserDetails -> {
            if (!idamDetailsRepository.existsById(emServiceAndUserDetails.getUsername())) {
                IdamDetails idamDetails = new IdamDetails();
                idamDetails.setId(emServiceAndUserDetails.getUsername());
                idamDetails.setForename(emServiceAndUserDetails.getForename());
                idamDetails.setSurname(emServiceAndUserDetails.getSurname());
                idamDetails.setEmail(emServiceAndUserDetails.getEmail());
                idamDetailsRepository.save(idamDetails);
            }
        });
    }

}
