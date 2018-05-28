import io.grpc.ManagedChannel;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CTabFolder;
//import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
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
        GridLayout layout = new GridLayout(2, true);
        final Color red = new Color(display,220,53,69);
        final Color black = new Color(display,30,30,30);
        Image logo = new Image(display, "images/tron-logo.png");
        shell.setSize(640, 500);
        shell.setBackground(black);
        shell.setLayout(layout);
//        Label logo_label = new Label(shell, SWT.None);
//        logo_label.setImage(logo);
//        logo_label.setBounds(100, 0, 50, 58);
//        logo_label.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));

//        Canvas canvas = new Canvas(shell, SWT.NONE);
//        canvas.addPaintListener(new PaintListener() {
//            public void paintControl(PaintEvent e) {
//                e.gc.drawImage(logo, 0, 0);
//            }
//        });

        // SWT.BOTTOM to show at the bottom
//        CTabFolder folder = new CTabFolder(shell, SWT.TOP);
//        GridData data = new GridData(SWT.FILL,
//                SWT.FILL, true, true,
//                4, 1);
//        folder.setLayoutData(data);
////        folder.setSelectionForeground(display.getSystemColor(SWT.COLOR_RED));
//        folder.setSelectionForeground(red);
//        CTabItem cTabItem1 = new CTabItem(folder, SWT.NONE);
//        cTabItem1.setText("Account");
//        CTabItem cTabItem2 = new CTabItem(folder, SWT.NONE);
//        cTabItem2.setText("Tron Power");
//        CTabItem cTabItem3 = new CTabItem(folder, SWT.NONE);
//        cTabItem3.setText("Vote");

        shell.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event) {
                event.gc.drawImage(logo, 250, 30);
//                Rectangle rect = shell.getClientArea();
//                ImageData data = logo.getImageData();
//
//                int srcX = data.width / 8;
//                int srcY = data.height / 8;
//                int srcWidth = data.width / 2;
//                int srcHeight = data.height / 2;
//                int destWidth = 2 * srcWidth;
//                int destHeight = 2 * srcHeight;

//                event.gc.drawImage(logo, srcX, srcY, srcWidth, srcHeight, rect.width
//                        - destWidth, rect.height - destHeight, destWidth, destHeight);
            }
        });

        MessageBox infoDialog =
                new MessageBox(shell, SWT.ICON_INFORMATION);
        infoDialog.setText("Info");

        MessageBox errorDialog =
                new MessageBox(shell, SWT.ICON_ERROR);
        errorDialog.setText("Error");

        // create new layout data
        GridData data4 = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        GridData data8 = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);

        Label private_key_label = new Label(shell, SWT.NONE);
        private_key_label.setForeground(red);
        private_key_label.setText("Private Key");
        Text private_key = new Text (shell, SWT.BORDER);
        private_key.setLayoutData(data8);

//        Label password_label = new Label(shell, SWT.NONE);
//        password_label.setText("Password");
//        Text password = new Text (shell, SWT.BORDER);
//        password.setLayoutData (new RowData (100, SWT.DEFAULT));

        Label address_label = new Label(shell, SWT.NONE);
        address_label.setForeground(red);
        address_label.setText("Address: ");
        address_label.setLayoutData(data8);

        Label balance_label = new Label(shell, SWT.NONE);
        balance_label.setForeground(red);
        balance_label.setText("Balance: ");
        balance_label.setLayoutData(data8);

        Label to_address_label = new Label(shell, SWT.NONE);
        to_address_label.setForeground(red);
        to_address_label.setText("To Address: ");
        Text to_address = new Text (shell, SWT.BORDER);
        to_address.setLayoutData(data8);

        Label to_amount_label = new Label(shell, SWT.NONE);
        to_amount_label.setForeground(red);
        to_amount_label.setText("To Amount (in TRX): ");
        Text to_amount = new Text (shell, SWT.BORDER);
        to_amount.setLayoutData(data4);

        Button login = new Button (shell, SWT.PUSH);
        login.setBackgroundImage(logo);
        login.setText ("Login");

        Button register = new Button (shell, SWT.PUSH);
        register.setText("Register");

        Button sendCoin = new Button (shell, SWT.PUSH);
        sendCoin.setText("Send Coin");
        sendCoin.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String toAddress = to_address.getText();
                String toAmount = to_amount.getText();
                boolean result = client.sendCoin("1234abcd", toAddress, Long.parseLong(toAmount) * 1000000);
                if (result) {
                    logger.info("Send " + toAmount + " drop to " + toAddress + " successful !!");
                    infoDialog.setMessage("Send " + toAmount + " drop to " + toAddress + " successful !!");
                    infoDialog.open();
                } else {
                    logger.info("Send " + toAmount + " drop to " + toAddress + " failed !!");
                    errorDialog.setMessage("Send " + toAmount + " drop to " + toAddress + " failed !!");
                    errorDialog.open();
                }
            }
        });
        sendCoin.setLayoutData(data4);

        Button backup_address = new Button (shell, SWT.PUSH);
        backup_address.setText("Copy Address");
        backup_address.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (account_address != null) {
                    logger.info("Backup a address successful !!");
                    logger.info("Address = " + account_address);
                    infoDialog.setMessage("Address = " + account_address);
                    infoDialog.open();
                }else{
                    logger.info("No address to copy !!");
                    errorDialog.setMessage("No address to copy !!");
                    errorDialog.open();
                }
            }
        });
        backup_address.setLayoutData(data4);

        Button backup = new Button (shell, SWT.PUSH);
        backup.setText("Copy Private Key");
        backup.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String priKey = client.backupWallet("1234abcd");
                if (priKey != null) {
                    logger.info("Backup a wallet successful !!");
                    logger.info("priKey = " + priKey);
                    infoDialog.setMessage("priKey = " + priKey);
                    infoDialog.open();
                }else{
                    logger.info("No private key to copy !!");
                    errorDialog.setMessage("No private key to copy !!");
                    errorDialog.open();
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
//
//        cTabItem1.setControl(shell);
//        cTabItem1.setControl(private_key_label);
//        cTabItem1.setControl(private_key);
//        cTabItem1.setControl(address_label);
//        cTabItem1.setControl(balance_label);
//        cTabItem1.setControl(login);
//        cTabItem1.setControl(register);
//        cTabItem1.setControl(backup);
//        cTabItem1.setControl(logout);

        // create a new label which is used as a separator
        Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(data8);

        Label frozen_balance_label = new Label(shell, SWT.NONE);
        frozen_balance_label.setForeground(red);
        frozen_balance_label.setText("Tron Power: ");
        frozen_balance_label.setLayoutData(data8);

        Label expire_time_label = new Label(shell, SWT.NONE);
        expire_time_label.setForeground(red);
        expire_time_label.setText("Expire Time: ");
        expire_time_label.setLayoutData(data8);

        Label freeze_amount_label = new Label(shell, SWT.NONE);
        freeze_amount_label.setForeground(red);
        freeze_amount_label.setText("Freeze Amount (in TRX): ");
        Text freeze_amount = new Text (shell, SWT.BORDER);
        freeze_amount.setLayoutData(data4);

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
        vote_address_label.setForeground(red);
        vote_address_label.setText("Witness Address: ");
        Text vote_address = new Text (shell, SWT.BORDER);
        vote_address.setLayoutData(data8);

        Label vote_amount_label = new Label(shell, SWT.NONE);
        vote_amount_label.setForeground(red);
        vote_amount_label.setText("Vote Amount (in TRX): ");
        Text vote_amount = new Text (shell, SWT.BORDER);
        vote_amount.setLayoutData(data4);

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
        logo.dispose();
        display.dispose();
    }

}
