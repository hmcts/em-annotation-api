package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.IdamDetailsRepository;
import uk.gov.hmcts.reform.em.annotation.service.IdamDetailsFilterService;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;

@Service
@Transactional
public class IdamDetailsFilterServiceImpl implements IdamDetailsFilterService {

    private final IdamDetailsRepository idamDetailsRepository;

    public IdamDetailsFilterServiceImpl(IdamDetailsRepository idamDetailsRepository) {
        this.idamDetailsRepository = idamDetailsRepository;
    }

    @Override
    public void saveIdamDetails(UserDetails userDetails) {

        if (!idamDetailsRepository.existsById(userDetails.getId())) {
            IdamDetails idamDetails = new IdamDetails();
            idamDetails.setId(userDetails.getId());
            idamDetails.setForename(userDetails.getForename());
            userDetails.getSurname().ifPresent(surname -> idamDetails.setSurname(surname));
            idamDetails.setEmail(userDetails.getEmail());
            idamDetailsRepository.save(idamDetails);
        }
    }

}
