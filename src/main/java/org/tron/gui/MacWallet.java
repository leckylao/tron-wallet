import io.grpc.ManagedChannel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.api.WalletGrpc;
import org.tron.api.WalletSolidityGrpc;
import org.tron.protos.Protocol;
import org.tron.walletcli.Client;

import java.util.Date;
import java.util.HashMap;

public class MacWallet
{
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
        GridLayout layout = new GridLayout(4, true);
        shell.setSize(640, 480);
        shell.setLayout(layout);

        MessageBox infoDialog =
                new MessageBox(shell, SWT.ICON_INFORMATION);
        infoDialog.setText("Info");

        MessageBox errorDialog =
                new MessageBox(shell, SWT.ICON_ERROR);
        errorDialog.setText("Error");

        // create new layout data
        GridData data4 = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
        GridData data8 = new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1);

        Label private_key_label = new Label(shell, SWT.NONE);
        private_key_label.setText("Private key");
        Text private_key = new Text (shell, SWT.BORDER);
//        private_key.setLayoutData (new RowData (100, SWT.DEFAULT));
        private_key.setLayoutData(data8);

//        Label password_label = new Label(shell, SWT.NONE);
//        password_label.setText("Password");
//        Text password = new Text (shell, SWT.BORDER);
//        password.setLayoutData (new RowData (100, SWT.DEFAULT));

//        Label status_label = new Label(shell, SWT.NONE);
//        status_label.setLayoutData(data8);

        Label address_label = new Label(shell, SWT.NONE);
        address_label.setText("Address: ");
        address_label.setLayoutData(data8);

        Label balance_label = new Label(shell, SWT.NONE);
        balance_label.setText("Balance: ");
        balance_label.setLayoutData(data8);

        Button login = new Button (shell, SWT.PUSH);
        login.setText ("Login");

        Button register = new Button (shell, SWT.PUSH);
        register.setText("Register");

        Button backup = new Button (shell, SWT.PUSH);
        backup.setText("Backup Wallet");
        backup.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String priKey = client.backupWallet("1234abcd");
                if (priKey != null) {
                    logger.info("Backup a wallet successful !!");
                    logger.info("priKey = " + priKey);
                    infoDialog.setMessage("priKey = " + priKey);
                    infoDialog.open();
                }
            }
        });
        backup.setLayoutData(data4);

        Button logout = new Button (shell, SWT.PUSH);
        logout.setText("Logout");
        logout.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                client.logout();
                logger.info("Logout successful !!!");
                infoDialog.setMessage("Logout Successful !");
                infoDialog.open();
                address_label.setText("");
                balance_label.setText("");
            }
        });
        logout.setLayoutData(data4);

        // create a new label which is used as a separator
        Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(data8);

        Label frozen_balance_label = new Label(shell, SWT.NONE);
        frozen_balance_label.setText("Tron Power: ");
        frozen_balance_label.setLayoutData(data8);

        Label expire_time_label = new Label(shell, SWT.NONE);
        expire_time_label.setText("Expire Time: ");
        expire_time_label.setLayoutData(data8);

        Label freeze_amount_label = new Label(shell, SWT.NONE);
        freeze_amount_label.setText("Freeze Amount (in TRX): ");
        Text freeze_amount = new Text (shell, SWT.BORDER);
        freeze_amount.setLayoutData(data8);

        login.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(private_key.getText().isEmpty()){
                    logger.info("Private key can't be blank");
                    errorDialog.setMessage("Private key can't be blank!");
                    errorDialog.open();
                }else {
                    if (client.importWallet("1234abcd", private_key.getText())) {
                        logger.info("Import a wallet and store it successful !!");
                        if (client.login("1234abcd")) {
                            logger.info("Login successful !");
                            infoDialog.setMessage("Login Successful !");
                            infoDialog.open();
                            account_address = client.getAddress();
                            logger.info("Address" + account_address);
                            address_label.setText("Address: " + account_address);
                            Protocol.Account account = client.queryAccount();
                            if (account == null) {
                                logger.info("Get Balance failed !!!!");
                            } else {
                                long balance = account.getBalance() / 1000000;
                                logger.info("Balance: " + balance);
                                balance_label.setText("Balance: " + String.valueOf(balance) + "TRX");
                                int frozenCount = account.getFrozenCount();
                                if(frozenCount == 0) {
                                    logger.info("No Tron Power");
                                }else {
                                    Protocol.Account.Frozen frozen = account.getFrozen(0);
                                    long frozenBalance = frozen.getFrozenBalance() / 1000000;
                                    long expireTime = frozen.getExpireTime();
                                    frozen_balance_label.setText("Tron Power: " + String.valueOf(frozenBalance) + "TRX");
                                    logger.info("frozenBalance: " + frozenBalance);
                                    logger.info("expireTime: " + expireTime);
                                    Date expire_date = new Date(expireTime);
                                    expire_time_label.setText("Expire Time: " + expire_date.toString());
                                }
                            }
                        }
                    } else {
                        logger.info("Import a wallet failed !!");
                        errorDialog.setMessage("Login Failed !");
                        errorDialog.open();
                    }
                }
            }
        });
        login.setLayoutData(data4);

        register.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (client.registerWallet("1234abcd")) {
                    logger.info("Register a wallet and store it successful !!");
                    if (client.login("1234abcd")) {
                        infoDialog.setMessage("Register Successful !");
                        infoDialog.open();
                        logger.info("Register successful !");
                        account_address = client.getAddress();
                        logger.info("Address" + account_address);
                        address_label.setText("Address: " + account_address);
                        Protocol.Account account = client.queryAccount();
                        if (account == null) {
                            logger.info("Get Balance failed !!!!");

                        } else {
                            long balance = account.getBalance() / 1000000;
                            logger.info("Balance: " + balance);
                            balance_label.setText("Balance: " + String.valueOf(balance) + "TRX");
                        }
                    }
                } else {
                    logger.info("Register wallet failed !!");
                    errorDialog.setMessage("Register Failed !");
                    errorDialog.open();
                }

            }
        });
        register.setLayoutData(data4);

//        Label freeze_duration_label = new Label(shell, SWT.NONE);
//        freeze_duration_label.setText("Freeze Duration: ");
//        Text freeze_duration = new Text (shell, SWT.BORDER);
//        freeze_duration.setLayoutData(data8);

        Button freeze = new Button (shell, SWT.PUSH);
        freeze.setText ("Freeze");
        freeze.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(freeze_amount.getText().isEmpty()){
                    errorDialog.setMessage("Freeze Amount can't be blank");
                    errorDialog.open();
                }else {
                    long frozen_balance = Long.parseLong(freeze_amount.getText()) * 1000000;
//                    long frozen_duration = 600000 + System.currentTimeMillis();
                    long frozen_duration = 3;
//                    long maxFrozenTime = dbManager.getDynamicPropertiesStore().getMaxFrozenTime();
//                    long minFrozenTime = dbManager.getDynamicPropertiesStore().getMinFrozenTime();

                    boolean result = client.freezeBalance("1234abcd", frozen_balance, frozen_duration);
                    if (result) {
                        logger.info("freezeBalance " + " successful !!");
                        infoDialog.setMessage("Freeze Balance Successful !");
                        infoDialog.open();
                    } else {
                        logger.info("freezeBalance " + " failed !!");
                        errorDialog.setMessage("Freeze Balance Failed !");
                        errorDialog.open();
                    }
                }
            }
        });
        freeze.setLayoutData(data4);

        Button unfreeze = new Button (shell, SWT.PUSH);
        unfreeze.setText ("Unfreeze");
        unfreeze.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                boolean result = client.unfreezeBalance("1234abcd");
                if (result) {
                    logger.info("unfreezeBalance " + " successful !!");
                    infoDialog.setMessage("Unfreeze Balance Successful !");
                    infoDialog.open();

                } else {
                    logger.info("unfreezeBalance " + " failed !!");
                    errorDialog.setMessage("Unable to unfreeze TRX. This could be caused because the minimal freeze period hasn't been reached yet.");
                    errorDialog.open();
                }
            }
        });
        unfreeze.setLayoutData(data4);

        // create a new label which is used as a separator
        label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(data8);

        Label vote_address_label = new Label(shell, SWT.NONE);
        vote_address_label.setText("Witness Address: ");
        Text vote_address = new Text (shell, SWT.BORDER);
        vote_address.setLayoutData(data8);

        Label vote_amount_label = new Label(shell, SWT.NONE);
        vote_amount_label.setText("Vote Amount (in TRX): ");
        Text vote_amount = new Text (shell, SWT.BORDER);
        vote_address.setLayoutData(data8);

        Button vote = new Button (shell, SWT.PUSH);
        vote.setText ("Vote Witness");
        vote.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(vote_address.getText().isEmpty() || vote_amount.getText().isEmpty()){
                    errorDialog.setMessage("Witness address and vote amount can't be blank");
                    errorDialog.open();
                }else {
                    HashMap<String, String> witness = new HashMap<String, String>();
                    witness.put(vote_address.getText(), vote_amount.getText());

                    boolean result = client.voteWitness("1234abcd", witness);
                    if (result) {
                        logger.info("VoteWitness " + " successful !!");
                        infoDialog.setMessage("Vote Witness Successful !");
                        infoDialog.open();
                    } else {
                        logger.info("VoteWitness " + " failed !!");
                        errorDialog.setMessage("Vote Witness Failed !");
                        errorDialog.open();
                    }
                }
            }
        });
        vote.setLayoutData(data4);

//        shell.setDefaultButton (vote);
//        shell.setLayout (new RowLayout ());
//        private_key_label.pack();
//        vote_address_label.pack();
//        shell.pack();
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();
    }

}
