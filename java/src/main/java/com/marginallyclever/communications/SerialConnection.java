package com.marginallyclever.communications;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.ArrayList;

import com.marginallyclever.communications.MarginallyCleverConnectionReadyListener;


/**
 * Created on 4/12/15.  Encapsulate all jssc serial receive/transmit implementation
 *
 * @author Peter Colapietro
 * @since v7
 */
public final class SerialConnection implements SerialPortEventListener, MarginallyCleverConnection {
	private SerialPort serialPort;
	private static final int BAUD_RATE = 57600;

	private String connectionName = "";
	private boolean portOpened = false;
	private boolean portConfirmed = false;
	private boolean waitingForCue = false;


	static final String CUE = "> ";
	static final String NOCHECKSUM = "NOCHECKSUM ";
	static final String BADCHECKSUM = "BADCHECKSUM ";
	static final String BADLINENUM = "BADLINENUM ";
	static final String NEWLINE = "\n";
	static final String COMMENT_START = ";";

	// parsing input from Makelangelo
	private String inputBuffer = "";
    ArrayList<String> commandQueue = new ArrayList<String>();
    
	// prevent repeating pings from appearing in console
	boolean lastLineWasCue = false;

    // Listeners which should be notified of a change to the percentage.
    private ArrayList<MarginallyCleverConnectionReadyListener> listeners = new ArrayList<MarginallyCleverConnectionReadyListener>();


	public SerialConnection() {}

	@Override
	public void sendMessage(String msg) throws Exception {
		try {
			serialPort.writeBytes(msg.getBytes());
		} catch (SerialPortException e) {
			throw new Exception(e.getMessage());
		}
	}


	@Override
	public void closeConnection() {
		if (portOpened) {
			if (serialPort != null) {
				try {
					serialPort.removeEventListener();
					serialPort.closePort();
				} catch (SerialPortException e) {
				}
			}
			portOpened = false;
			portConfirmed = false;
		}
	}

	// open a serial connection to a device.  We won't know it's the robot until
	@Override
	public void openConnection(String portName) throws Exception {
		if (portOpened) return;

		closeConnection();

		// open the port
		serialPort = new SerialPort(portName);
		serialPort.openPort();// Open serial port
		serialPort.setParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		serialPort.addEventListener(this);

		connectionName = portName;
		portOpened = true;
		lastLineWasCue = false;
	}


	/**
	 * Check if the robot reports an error and if so what line number.
	 *
	 * @return -1 if there was no error, otherwise the line number containing the error.
	 */
	protected int errorReported() {
		if (portConfirmed == false) return -1;

		if (inputBuffer.lastIndexOf(NOCHECKSUM) != -1) {
			String after_error = inputBuffer.substring(inputBuffer.lastIndexOf(NOCHECKSUM) + NOCHECKSUM.length());
			String x = getNumberPortion(after_error);
			int err = 0;
			try {
				err = Integer.decode(x);
			} catch (Exception e) {
			}

			return err;
		}
		if (inputBuffer.lastIndexOf(BADCHECKSUM) != -1) {
			String after_error = inputBuffer.substring(inputBuffer.lastIndexOf(BADCHECKSUM) + BADCHECKSUM.length());
			String x = getNumberPortion(after_error);
			int err = 0;
			try {
				err = Integer.decode(x);
			} catch (Exception e) {
			}

			return err;
		}
		if (inputBuffer.lastIndexOf(BADLINENUM) != -1) {
			String after_error = inputBuffer.substring(inputBuffer.lastIndexOf(BADLINENUM) + BADLINENUM.length());
			String x = getNumberPortion(after_error);
			int err = 0;
			try {
				err = Integer.decode(x);
			} catch (Exception e) {
			}

			return err;
		}

		return -1;
	}


	// Deal with something robot has sent.
	@Override
	public void serialEvent(SerialPortEvent events) {
		String rawInput, oneLine;
		int x;
		
        if(events.isRXCHAR()) {
        	if(!portOpened) return;
            try {
            	int len = events.getEventValue();
				byte [] buffer = serialPort.readBytes(len);
				if( len>0 ) {
					rawInput = new String(buffer,0,len);
//					Log(rawInput);
					inputBuffer+=rawInput;
					// each line ends with a \n.
					for( x=inputBuffer.indexOf("\n"); x!=-1; x=inputBuffer.indexOf("\n") ) {
						x=x+1;
						oneLine = inputBuffer.substring(0,x);
						inputBuffer = inputBuffer.substring(x);
						processLine(oneLine);
						// wait for the cue to send another command
						if(oneLine.indexOf(CUE)==0) {
							waitingForCue=false;
						}
					}
					if(waitingForCue==false) {
						sendQueuedCommand();
					}
				}
            } catch (SerialPortException e) {}
        }
	}


	protected void sendQueuedCommand() {
		if(!portOpened || waitingForCue) return;
		
		if(commandQueue.size()==0) {
		      notifyListeners();
		      return;
		}
		
		String command;
		try {
			command=commandQueue.remove(0);
			String line = command;
			if(line.contains(COMMENT_START)) {
				String [] lines = line.split(COMMENT_START);
				command = lines[0];
			}
			//log(command+NEWLINE);
			line+=NEWLINE;
			serialPort.writeBytes(line.getBytes());
			waitingForCue=true;
		}
		catch(IndexOutOfBoundsException e1) {}
		catch(SerialPortException e2) {}
	}
	
	public void deleteAllQueuedCommands() {
		commandQueue.clear();
	}
	
	// connect to the last port
	@Override
	public void reconnect() throws Exception {
		openConnection(connectionName);
	}

	/**
	 * Java string to int is very picky.  this method is slightly less picky.  Only works with positive whole numbers.
	 *
	 * @param src
	 * @return the portion of the string that is actually a number
	 */
	private String getNumberPortion(String src) {
		src = src.trim();
		int length = src.length();
		String result = "";
		for (int i = 0; i < length; i++) {
			Character character = src.charAt(i);
			if (Character.isDigit(character)) {
				result += character;
			}
		}
		return result;
	}


	/**
	 * @return <code>true</code> if the serial port has been confirmed; <code>false</code> otherwise
	 */
	@Override
	public boolean isRobotConfirmed() {
		return portConfirmed;
	}

	/**
	 * @return the port open for this serial connection.
	 */
	@Override
	public boolean isConnectionOpen() {
		return portOpened;
	}

	@Override
	public String getRecentConnection() {
		return connectionName;
	}

	@Override
	public void addListener(MarginallyCleverConnectionReadyListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListener(MarginallyCleverConnectionReadyListener listener) {
		// TODO Auto-generated method stub

	}

    private void notifyListeners() {
      for (MarginallyCleverConnectionReadyListener listener : listeners) {
        listener.serialConnectionReady(this);
      }
    }
	
	// tell all listeners data has arrived
	private void processLine(String line) {
	      for (MarginallyCleverConnectionReadyListener listener : listeners) {
	        listener.serialDataAvailable(this,line);
	      }
	}
}
