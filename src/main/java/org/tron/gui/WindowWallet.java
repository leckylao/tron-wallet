import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.protos.Protocol;
import org.tron.walletcli.Client;

public class WindowWallet
{
    public static void main( String[] args ) {
        new WindowWallet().run();
    }

    private Shell shell;
    private ScrolledComposite scrolledComposite;

    void run() {
        createControls();
        layoutControls();
        openShell();
        runEventLoop();
    }

    private void createControls() {
        shell = new Shell( new Display() );
        shell.setText( "Contact Details Form" );
        shell.addListener( SWT.Dispose, event -> event.display.dispose() );
        scrolledComposite = new ScrolledComposite( shell, SWT.V_SCROLL );
        Composite parent = createForm();
        scrolledComposite.setContent( parent );
        scrolledComposite.setExpandVertical( true );
        scrolledComposite.setExpandHorizontal( true );
        scrolledComposite.addListener( SWT.Resize, event -> {
            int width = scrolledComposite.getClientArea().width;
            scrolledComposite.setMinSize( parent.computeSize( width, SWT.DEFAULT ) );
        } );
    }

    private void layoutControls() {
        shell.setLayout( new GridLayout( 1, false ) );
        GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
//        gridData.heightHint = computePreferredHeight();
        scrolledComposite.setLayoutData( gridData );
    }

    private void openShell() {
        shell.pack();
        shell.open();
    }

    private void runEventLoop() {
        while( !shell.isDisposed() ) {
            if( !shell.getDisplay().readAndDispatch() ) {
                shell.getDisplay().sleep();
            }
        }
    }

    private Composite createForm() {
        Composite result = new Composite( scrolledComposite, SWT.NONE );
        result.setLayout( new GridLayout( 2, false ) );
        createField( result, "Title" );
        createField( result, "Salutation" );
        createField( result, "First name" );
        createField( result, "Middle name" );
        createField( result, "Last name" );
        createField( result, "Maiden name" );
        createField( result, "Street" );
        createField( result, "Street supplement" );
        createField( result, "PO Box" );
        createField( result, "Zip code" );
        createField( result, "City" );
        createField( result, "State" );
        createField( result, "Country" );
        createField( result, "Private Phone" );
        createField( result, "Mobile Phone" );
        createField( result, "Email address" );
        createField( result, "Web site" );
        return result;
    }

    private static void createField( Composite parent, String labelText ) {
        Label label = new Label( parent, SWT.NONE );
        label.setText( labelText );
        Text text = new Text( parent, SWT.BORDER );
        text.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    }

}