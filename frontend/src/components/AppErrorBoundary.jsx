import React from "react";

class AppErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, message: "" };
  }

  static getDerivedStateFromError(error) {
    return {
      hasError: true,
      message: error?.message || "Unexpected render error",
    };
  }

  componentDidCatch(error, errorInfo) {
    // Keep details in console for production troubleshooting.
    console.error("AppErrorBoundary caught error:", error, errorInfo);
  }

  handleReload = () => {
    window.location.reload();
  };

  render() {
    if (this.state.hasError) {
      return (
        <div className="cfr-page-bg" style={{ minHeight: "100vh", display: "grid", placeItems: "center" }}>
          <div className="card cfr-card p-4" style={{ maxWidth: "680px", margin: "1rem" }}>
            <h2 className="mb-3">UI Error Detected</h2>
            <p className="mb-2">
              The application hit a client-side rendering error. This usually means a stale or mismatched JS bundle is loaded.
            </p>
            <p className="mb-3 text-muted">Details: {this.state.message}</p>
            <button type="button" className="btn cfr-primary text-white" onClick={this.handleReload}>
              Reload Page
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export default AppErrorBoundary;
