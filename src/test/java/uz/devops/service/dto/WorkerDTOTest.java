package uz.devops.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uz.devops.web.rest.TestUtil;

class WorkerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkerDTO.class);
        WorkerDTO workerDTO1 = new WorkerDTO();
        workerDTO1.setId(1L);
        WorkerDTO workerDTO2 = new WorkerDTO();
        assertThat(workerDTO1).isNotEqualTo(workerDTO2);
        workerDTO2.setId(workerDTO1.getId());
        assertThat(workerDTO1).isEqualTo(workerDTO2);
        workerDTO2.setId(2L);
        assertThat(workerDTO1).isNotEqualTo(workerDTO2);
        workerDTO1.setId(null);
        assertThat(workerDTO1).isNotEqualTo(workerDTO2);
    }
}
