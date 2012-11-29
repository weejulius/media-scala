package com.thenetcircle.services.media.service.rest.features;


import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.matchers.JUnitMatchers;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class SchemaRawSteps
{
  private static final String HOST="http://jyu:8080/";
  private String schemaRaw;
  private String url;
  private String actionMethod;
  private String responseMessage;
  private int responseCode;

  @Given("^I input a schema$")
  public void iInputASchema(final String schema)
  {
    schemaRaw=schema;
  }

  @And("^the URL is \"(.*)\"$")
  public void iAmOnThePage(final String url)
  {
    this.url=HOST+url;
  }


  @When("^I send request by (.*)$")
  public void iSendRequestBy(final String method) throws Exception
  {
    actionMethod=method;
    executeHttp(url, actionMethod);
  }

  @When("^the schema \"(.*)\" is not existing")
  public void theSchemaIsNotExisting(final String schemaName) throws Exception
  {
    executeHttp(HOST+"media/rest/schemas/"+schemaName, "delete");
  }

  @Then("^I should see the message:$")
  public void iShouldSeeTheMessage(final String expectedFeedback)
  {
    Assert.assertThat(responseMessage, JUnitMatchers.containsString(expectedFeedback));
  }


  @Then("^I should see the response code (\\d+)$")
  public void iShouldSeeTheResponseCode(final int responseCode)
  {
    Assert.assertEquals(responseCode, this.responseCode);
  }

  @After
  public void tearDown()
  {
    responseMessage=null;
    responseCode=-1;
  }

  private void executeHttp(final String url, final String actionMethod) throws Exception
  {
    HttpClient httpclient=new DefaultHttpClient();

    HttpRequestBase httpMethod;

    switch(actionMethod)
    {
      case "get":
        httpMethod=new HttpGet(url);
        break;
      case "post":
        httpMethod=new HttpPost(url);
        if(schemaRaw!=null)
          ((HttpPost)httpMethod).setEntity(new StringEntity(schemaRaw));
        break;
      case "put":
        httpMethod=new HttpPut(url);
        if(schemaRaw!=null)
          ((HttpPut)httpMethod).setEntity(new StringEntity(schemaRaw));
        break;
      case "delete":
        httpMethod=new HttpDelete(url);
        break;
      default:
        httpMethod=null;
    }
    HttpResponse response=httpclient.execute(httpMethod);
    responseCode=response.getStatusLine().getStatusCode();
    if(response.getEntity()!=null)
    {
      BufferedReader br=new BufferedReader(
        new InputStreamReader((response.getEntity().getContent())));

      String output="";
      responseMessage="";
      while((output=br.readLine())!=null)
      {
        responseMessage+=output+"\n";
      }

    }
    httpclient.getConnectionManager().shutdown();

  }
}
