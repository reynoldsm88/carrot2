import "./ErrorMessage.css";

import React from "react";

import { view } from "react-easy-state";
import { ShowHide } from "./Optional.js";

import { sources } from "../../config-sources.js";
import { branding } from "../../config-branding.js";

export const ErrorMessage = props => {
  return (
    <ShowHide visible={props.error !== undefined} className="Error">
      {props.children}
      <pre>{props.error && props.error.statusText.replace(/([&/?])/g, "$1\u200b")}</pre>
      <p>That's all we know.</p>
    </ShowHide>
  );
};

export const SearchEngineErrorMessage = view(props => {
  const source = sources[props.source];
  return source.createError(props);
});

export const GenericSearchEngineErrorMessage = view(props => {
  return (
    <ErrorMessage error={props.store.error}>
      <h3>Search engine error</h3>
      <p>Search could not be performed due to the following error:</p>
    </ErrorMessage>
  );
});

export const ClusteringServerRateLimitExceededError = () => {
  return (
    <div className="Error">
      <h3>Too many clustering requests :-(</h3>

      <p>
        You are making too many clustering requests for our little demo
        server to handle. Please check back in a minute.
      </p>

      <p>
        <small className="light">
          {branding.createUnlimitedDistributionInfo()}
        </small>
      </p>
    </div>
  );
};

export const ClusteringRequestSizeLimitExceededError = () => {
  return (
    <div className="Error">
      <h3>Too much data to cluster :-(</h3>

      <p>
        You sent too much data for our little demo
        server to handle. Lower the number of search results and try again.
      </p>

      <p>
        <small className="light">
          {branding.createUnlimitedDistributionInfo()}
        </small>
      </p>
    </div>
  );
};

export const ClusteringExceptionMessage = ({ exception }) => {
  const lines = exception.stacktrace.split(/\n/);

  return (
    <div className="Error">
      <h3>Clustering engine error</h3>

      <p>
        Results could not be clustered due to the following error:
      </p>

      <p>
        <strong>{lines[0]}</strong>
      </p>

      <p>That's all we know.</p>
    </div>
  );
};

export const ClusteringErrorMessage = ({ message }) => {
  return (
      <div className="Error">
        <h3>Clustering engine error</h3>

        <p>
          Results could not be clustered due to the following error:
        </p>

        <p>
          <strong>{message}</strong>
        </p>

        <p>That's all we know.</p>
      </div>
  );
};

export const ClusteringEngineErrorMessage = view(props => {
  const error = props.store.error;
  if (error && error.status === 429) {
    return <ClusteringServerRateLimitExceededError />;
  }

  if (error && error.status === 413) {
    return <ClusteringRequestSizeLimitExceededError />;
  }
  if (error && error.bodyParsed) {
    if (error.bodyParsed.stacktrace) {
      return <ClusteringExceptionMessage exception={error.bodyParsed}/>
    }
    if (error.bodyParsed.message) {
      return <ClusteringErrorMessage message={error.bodyParsed.message}/>
    }
  }

  return (
    <ErrorMessage error={props.store.error}>
      <h3>Clustering engine error</h3>
      <p>Results could not be clustered due to the following error:</p>
    </ErrorMessage>
  );
});