package objectiveLabs.insistenceLayer.infra;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.google.common.base.Preconditions;

public class InsistenceLayerDataSource implements DataSource {
	
	private static ConnectionWrapper connectionWrapper;
	
	private ConnectionBeanDigester connectionBeanDigester = new ConnectionBeanDigester();
	private ConnectionBean connectionBean;
	
	public InsistenceLayerDataSource() { 
		// if used, the standard constructor the xml config will be used.
	}
	
	public InsistenceLayerDataSource(ConnectionBean connectionBean) {
		Preconditions.checkNotNull(connectionBean, "ConnectionBean is null");
		this.connectionBean = connectionBean;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return DriverManager.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		DriverManager.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		DriverManager.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return DriverManager.getLoginTimeout();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException("unwrap");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException("isWrapperFor");
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnection();
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		if (connectionBean == null) initializeConnectionBeanByDefaultXML();
		return getConnectionWrapper();
	}
	
	private void initializeConnectionBeanByDefaultXML() {
		connectionBean = connectionBeanDigester.digesterDefault();
	}
	
	private Connection getConnectionWrapper() throws SQLException {
		if (connectionWrapper == null) connectionWrapper = new ConnectionWrapper(newConnection());
		return connectionWrapper;
	}
	
	private Connection newConnection() throws SQLException {
		return DriverManager.getConnection(connectionBean.getUrl(), connectionBean.getUsername(), connectionBean.getPassword());
	}

}