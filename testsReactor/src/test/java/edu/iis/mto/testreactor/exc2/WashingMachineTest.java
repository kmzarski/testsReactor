package edu.iis.mto.testreactor.exc2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WashingMachineTest {
    @Mock
    private DirtDetector dirtDetector;
    @Mock
    private Engine engine;
    @Mock
    private WaterPump waterPump;

    private WashingMachine washingMachine;
    private LaundryBatch laundryBatch;
    private ProgramConfiguration programConfiguration;

    @Before
    public void init() {
        washingMachine = new WashingMachine(dirtDetector, engine, waterPump);
       laundryBatch = LaundryBatch.builder().withWeightKg(1000).withType(Material.COTTON).build();
        programConfiguration = ProgramConfiguration.builder().withProgram(Program.AUTODETECT).withSpin(false).build();
    }

    @Test
    public void itCompiles() {
        assertThat(true, Matchers.equalTo(true));
    }

    @Test
    public void shouldReturnTooHeavy() {
        LaundryStatus laundryStatus = washingMachine.start(laundryBatch, programConfiguration);
        LaundryStatus expectedLaundryStatus = LaundryStatus.builder()
                .withResult(Result.FAILURE)
                .withErrorCode(ErrorCode.TOO_HEAVY)
                .build();
        assertThat(laundryStatus.getErrorCode(), is(expectedLaundryStatus.getErrorCode()));
    }
}
