package uz.devops.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkHistoryMapperTest {

    private WorkHistoryMapper workHistoryMapper;

    @BeforeEach
    public void setUp() {
        workHistoryMapper = new WorkHistoryMapperImpl();
    }
}
