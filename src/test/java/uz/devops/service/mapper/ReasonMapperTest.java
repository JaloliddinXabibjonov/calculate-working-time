package uz.devops.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReasonMapperTest {

    private ReasonMapper reasonMapper;

    @BeforeEach
    public void setUp() {
        reasonMapper = new ReasonMapperImpl();
    }
}
