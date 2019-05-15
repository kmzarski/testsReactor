package edu.iis.mto.testreactor.exc2;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
        laundryBatch = LaundryBatch.builder().withWeightKg(6).withType(Material.COTTON).build();
        programConfiguration = ProgramConfiguration.builder().withProgram(Program.AUTODETECT).withSpin(false).build();
        when(dirtDetector.detectDirtDegree(any())).thenReturn(new Percentage(3));
    }

    @Test
    public void itCompiles() {
        assertThat(true, Matchers.equalTo(true));
    }

    @Test
    public void shouldReturnTooHeavyWhenIsOverload() {
        laundryBatch = LaundryBatch.builder().withWeightKg(1000).withType(Material.COTTON).build();
        LaundryStatus expectedLaundryStatus = LaundryStatus.builder()
                .withResult(Result.FAILURE)
                .withErrorCode(ErrorCode.TOO_HEAVY)
                .build();

        LaundryStatus laundryStatus = washingMachine.start(laundryBatch, programConfiguration);

        assertThat(laundryStatus.getErrorCode(), is(expectedLaundryStatus.getErrorCode()));
    }

    @Test
    public void shouldUseDetectDirtWhenProgramIsAutodetect() {
        washingMachine.start(laundryBatch, programConfiguration);
        verify(dirtDetector, atLeastOnce()).detectDirtDegree(laundryBatch);
        assertThat(true, is(true));
    }

    @Test
    public void shouldReturnSuccesWithNormalWeight() {
        LaundryStatus expectedLaundryStatus = LaundryStatus.builder()
                .withResult(Result.SUCCESS)
                .withRunnedProgram(Program.AUTODETECT)
                .build();

        LaundryStatus laundryStatus = washingMachine.start(laundryBatch, programConfiguration);
        assertThat(laundryStatus.getResult(), is(expectedLaundryStatus.getResult()));
    }

    @Test
    public void shouldUsePompAndEngineMethodAfterWashingMachineStart() {
        washingMachine.start(laundryBatch, programConfiguration);
        verify(waterPump, atLeastOnce()).pour(6.0);
        verify(engine, atLeastOnce()).runWashing(50);
        verify(waterPump, atLeastOnce()).release();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionAfterMinusValueCreatingPercentage() {
        new Percentage(-10);
    }
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionAfterGreaterThen100ValueCreatingPercentage() {
        new Percentage(101);
    }
}
