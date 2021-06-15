/**
 * This work is protected under copyrights held by the members of the
 * TOOP Project Consortium as indicated at
 * http://wiki.ds.unipi.gr/display/TOOP/Contributors
 * (c) 2018-2021. All rights reserved.
 *
 * This work is licensed under the EUPL 1.2.
 *
 *  = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *         https://joinup.ec.europa.eu/software/page/eupl
 */
package eu.toop.dsd.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.Source;

import org.w3c.dom.Element;

import com.helger.regrep.RegRep4Reader;
import com.helger.regrep.query.QueryResponse;
import com.helger.regrep.rim.AnyValueType;

import eu.toop.edm.jaxb.dcatap.DCatAPDatasetType;
import eu.toop.edm.xml.cagv.CCAGV;
import eu.toop.edm.xml.dcatap.DatasetMarshaller;

public class DcatDatasetTypeReader {

  public static final String DATASET_SLOT_NAME = "Dataset";

  /**
   * Instantiates a new DcatDatasetTypeReader
   */
  /* hide the constructor */
  private DcatDatasetTypeReader() {
  }

  /**
   * Read match type list from a {@link Source} object
   *
   * @param aSource the {@link Source} object
   * @return the list of {@link DCatAPDatasetType} objects
   */
  @Nullable
  public static List<DCatAPDatasetType> parseDataset(@Nonnull final Source aSource) {
    QueryResponse queryResponse = RegRep4Reader.queryResponse(CCAGV.XSDS).read(aSource);
    if (queryResponse == null)
      return null;

    return convertQueryResponseToDCatAPDatasetTypeList(queryResponse);
  }

  private static List<DCatAPDatasetType> convertQueryResponseToDCatAPDatasetTypeList(QueryResponse queryResponse) {
    List<Element> dcatElements = new ArrayList<>();

    queryResponse.getRegistryObjectList().getRegistryObject().forEach(registryObjectType -> {
      registryObjectType.getSlot().forEach(slotType -> {
        if (DATASET_SLOT_NAME.equals(slotType.getName())) {
          //this must be a dataset.
          Element dcatElement = (Element) ((AnyValueType) slotType.getSlotValue()).getAny();
          dcatElements.add(dcatElement);
        }
      });
    });

    return convertElementsToDCatList(dcatElements);
  }

  /**
   * Convert the list of {@link Element} objects to a List of {@link DCatAPDatasetType} objects
   * @param dcatElements the list of {@link Element} objects. Not empty
   * @return the list of {@link DCatAPDatasetType} objects
   */
  private static List<DCatAPDatasetType> convertElementsToDCatList(List<Element> dcatElements) {
    List<DCatAPDatasetType> datasetTypes = new ArrayList<>(dcatElements.size());

    DatasetMarshaller dm = new DatasetMarshaller();
    dcatElements.forEach(element -> {
      DCatAPDatasetType dataset = dm.read(element);
      datasetTypes.add(dataset);
    });

    return datasetTypes;
  }
}
