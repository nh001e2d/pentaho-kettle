package org.pentaho.di.trans.steps.blockingstep;

import org.junit.Test;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class BlockingStep_PDI_11344_Test {

  private static RowMetaInterface createRowMetaInterface() {
    RowMetaInterface rm = new RowMeta();

    ValueMetaInterface[] valuesMeta =
    {
      new ValueMeta( "field1", ValueMeta.TYPE_STRING ), new ValueMeta( "field2", ValueMeta.TYPE_INTEGER ),
      new ValueMeta( "field3", ValueMeta.TYPE_NUMBER ), new ValueMeta( "field4", ValueMeta.TYPE_DATE ),
      new ValueMeta( "field5", ValueMeta.TYPE_BOOLEAN ),
      new ValueMeta( "field6", ValueMeta.TYPE_BIGNUMBER ),
      new ValueMeta( "field7", ValueMeta.TYPE_BIGNUMBER ) };

    for ( ValueMetaInterface aValuesMeta : valuesMeta ) {
      rm.addValueMeta( aValuesMeta );
    }

    return rm;
  }

  @Test
  public void outputRowMetaIsCreateOnce() throws Exception {
    StepMockHelper<BlockingStepMeta, BlockingStepData> mockHelper =
      new StepMockHelper<BlockingStepMeta, BlockingStepData>( "BlockingStep", BlockingStepMeta.class,
        BlockingStepData.class );
    when( mockHelper.logChannelInterfaceFactory.create( any(), any( LoggingObjectInterface.class ) ) )
      .thenReturn( mockHelper.logChannelInterface );
    when( mockHelper.trans.isRunning() ).thenReturn( true );

    BlockingStep step =
      new BlockingStep( mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta,
        mockHelper.trans );
    step = spy( step );

    BlockingStepData data = new BlockingStepData();
    step.init( mockHelper.processRowsStepMetaInterface, data );
    step.setInputRowMeta( createRowMetaInterface() );

    doReturn( new Object[ 0 ] ).when( step ).getRow();
    step.processRow( mockHelper.processRowsStepMetaInterface, data );

    RowMetaInterface outputRowMeta = data.outputRowMeta;
    assertNotNull( outputRowMeta );

    doReturn( new Object[ 0 ] ).when( step ).getRow();
    step.processRow( mockHelper.processRowsStepMetaInterface, data );
    assertTrue( data.outputRowMeta == outputRowMeta );
  }
}
