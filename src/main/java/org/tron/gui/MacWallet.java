import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import com.google.protobuf.ByteString;
import com.google.protobuf.Struct;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.tron.api.WalletGrpc;
import org.tron.api.WalletSolidityGrpc;
import org.tron.common.crypto.ECKey;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Utils;
import org.tron.core.config.Configuration;
import org.tron.protos.Protocol;
import org.tron.walletserver.WalletClient;
import org.tron.walletcli.Client;

public class MacWallet
{
    public static String account_private_key;
    public static String account_address;

    private static ManagedChannel channelFull = null;
    private static ManagedChannel channelSolidity = null;
    private static WalletGrpc.WalletBlockingStub blockingStubFull = null;
    private static WalletSolidityGrpc.WalletSolidityBlockingStub blockingStubSolidity = null;

    private static final Logger logger = LoggerFactory.getLogger("MacWallet");
    private static Client client = new Client();

    public static void main (String[] args)
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        // create a new GridLayout with two columns
        // of different size
        GridLayout layout = new GridLayout(8, false);
        shell.setLayout(layout);

        // create new layout data
        GridData data4 = new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1);
        GridData data8 = new GridData(SWT.FILL, SWT.TOP, true, false, 8, 1);

        Label private_key_label = new Label(shell, SWT.NONE);
        private_key_label.setText("Private key");
        Text private_key = new Text (shell, SWT.BORDER);
//        private_key.setLayoutData (new RowData (100, SWT.DEFAULT));
        private_key.setLayoutData(data8);

        Label vote_address_label = new Label(shell, SWT.NONE);
        vote_address_label.setText("Vote Address");
        Text vote_address = new Text (shell, SWT.BORDER);
//        vote_address.setLayoutData (new RowData (100, SWT.DEFAULT));
        vote_address.setLayoutData(data8);

//        Label password_label = new Label(shell, SWT.NONE);
//        password_label.setText("Password");
//        Text password = new Text (shell, SWT.BORDER);
//        password.setLayoutData (new RowData (100, SWT.DEFAULT));

        Label status_label = new Label(shell, SWT.NONE);
        status_label.setLayoutData(data8);

        Label address_label = new Label(shell, SWT.NONE);
        address_label.setText("Address: ");
        address_label.setLayoutData(data8);

        Label balance_label = new Label(shell, SWT.NONE);
        balance_label.setText("Balance: ");
        balance_label.setLayoutData(data8);

        Button login = new Button (shell, SWT.PUSH);
        login.setText ("Login");
        //login.addSelectionListener(widgetSelectedAdapter(e -> System.out.println(text.getText())));
        login.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(private_key.getText() != null)
                {
                    if (client.importWallet("1234abcd", private_key.getText())) {
                        logger.info("Import a wallet and store it successful !!");
                        if (client.login("1234abcd")) {
                            status_label.setText("Login successful !");
                            logger.info("Login successful !");
                            account_address = client.getAddress();
                            logger.info("Address" + account_address);
                            address_label.setText("Address: " + account_address);
                            Protocol.Account account = client.queryAccount();
                            if (account == null) {
                                logger.info("Get Balance failed !!!!");
                            } else {
                                long balance = account.getBalance();
                                logger.info("Balance: " + balance);
                                balance_label.setText("Balance: " + String.valueOf(balance));
                            }
                        }
                    } else {
                        logger.info("Import a wallet failed !!");
                        status_label.setText("Import failed !");
                    }
                }
                else{
                    System.out.println("Private key can't be blank");
                    status_label.setText("Private key can't be blank");
                }
            }
        });
        login.setLayoutData(data4);

        Button register = new Button (shell, SWT.PUSH);
        register.setText("Register");
        register.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                    if (client.registerWallet("1234abcd")) {
                        logger.info("Register a wallet and store it successful !!");
                        if (client.login("1234abcd")) {
                            status_label.setText("Login successful !");
                            logger.info("Login successful !");
                            account_address = client.getAddress();
                            logger.info("Address" + account_address);
                            address_label.setText("Address: " + account_address);
                            Protocol.Account account = client.queryAccount();
                            if (account == null) {
                                logger.info("Get Balance failed !!!!");

                            } else {
                                long balance = account.getBalance();
                                logger.info("Balance: " + balance);
                                balance_label.setText("Balance: " + String.valueOf(balance));
                            }
                        }
                    } else {
                        logger.info("Register wallet failed !!");
                        status_label.setText("Register wallet failed !");
                    }

            }
        });
        register.setLayoutData(data4);

        Button logout = new Button (shell, SWT.PUSH);
        logout.setText("Logout");
        logout.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                client.logout();
                logger.info("Logout successful !!!");
                status_label.setText("Logout successful !");
                address_label.setText("");
                balance_label.setText("");
            }
        });
        logout.setLayoutData(data4);

        Button vote = new Button (shell, SWT.PUSH);
        vote.setText ("Vote");
        vote.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {


            }
        });
        vote.setLayoutData(data4);
//        shell.setDefaultButton (vote);
//        shell.setLayout (new RowLayout ());
//        private_key_label.pack();
//        vote_address_label.pack();
        shell.pack();
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();
    }

}
