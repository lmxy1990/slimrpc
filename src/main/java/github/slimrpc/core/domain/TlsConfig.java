package github.slimrpc.core.domain;

public class TlsConfig {
	private String tlsKeyStoreFilePath;
	private String tlsKeyStorePassword;
	private String tlsKeyPassword;
	private String tlsCertStorePath;
	private String tlsCertStorePassword;

	public String getTlsKeyStoreFilePath() {
		return tlsKeyStoreFilePath;
	}

	public void setTlsKeyStoreFilePath(String tlsKeyStoreFilePath) {
		this.tlsKeyStoreFilePath = tlsKeyStoreFilePath;
	}

	public String getTlsKeyStorePassword() {
		return tlsKeyStorePassword;
	}

	public void setTlsKeyStorePassword(String tlsKeyStorePassword) {
		this.tlsKeyStorePassword = tlsKeyStorePassword;
	}

	public String getTlsKeyPassword() {
		return tlsKeyPassword;
	}

	public void setTlsKeyPassword(String tlsKeyPassword) {
		this.tlsKeyPassword = tlsKeyPassword;
	}

	public String getTlsCertStorePath() {
		return tlsCertStorePath;
	}

	public void setTlsCertStorePath(String tlsCertStorePath) {
		this.tlsCertStorePath = tlsCertStorePath;
	}

	public String getTlsCertStorePassword() {
		return tlsCertStorePassword;
	}

	public void setTlsCertStorePassword(String tlsCertStorePassword) {
		this.tlsCertStorePassword = tlsCertStorePassword;
	}

}
