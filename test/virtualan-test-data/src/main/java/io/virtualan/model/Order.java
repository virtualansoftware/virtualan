package io.virtualan.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
  * An order for a pets from the pet store
 **/
public class Order  {
  
  private Long id = null;

  private Long petId = null;

  private Integer quantity = null;

  private Date shipDate = null;


@XmlType(name="StatusEnum")
@XmlEnum(String.class)
public enum StatusEnum {

@XmlEnumValue("placed") PLACED(String.valueOf("placed")), @XmlEnumValue("approved") APPROVED(String.valueOf("approved")), @XmlEnumValue("delivered") DELIVERED(String.valueOf("delivered"));


    private String value;

    StatusEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static StatusEnum fromValue(String v) {
        for (StatusEnum b : StatusEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}

 /**
   * Order Status  
  **/
  private StatusEnum status = null;

  private Boolean complete = false;
 /**
   * Get id
   * @return id
  **/
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Order id(Long id) {
    this.id = id;
    return this;
  }

 /**
   * Get petId
   * @return petId
  **/
  public Long getPetId() {
    return petId;
  }

  public void setPetId(Long petId) {
    this.petId = petId;
  }

  public Order petId(Long petId) {
    this.petId = petId;
    return this;
  }

 /**
   * Get quantity
   * @return quantity
  **/
  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Order quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

 /**
   * Get shipDate
   * @return shipDate
  **/
  public Date getShipDate() {
    return shipDate;
  }

  public void setShipDate(Date shipDate) {
    this.shipDate = shipDate;
  }

  public Order shipDate(Date shipDate) {
    this.shipDate = shipDate;
    return this;
  }

 /**
   * Order Status
   * @return status
  **/
  public String getStatus() {
    if (status == null) {
      return null;
    }
    return status.value();
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public Order status(StatusEnum status) {
    this.status = status;
    return this;
  }

 /**
   * Get complete
   * @return complete
  **/
  public Boolean isComplete() {
    return complete;
  }

  public void setComplete(Boolean complete) {
    this.complete = complete;
  }

  public Order complete(Boolean complete) {
    this.complete = complete;
    return this;
  }




}

