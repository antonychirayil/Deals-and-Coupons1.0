export class coupon{

  constructor(
    public  id: string,
    public  provider: string,
    public  code: string,
    public  category: string,
    public  description: string,
    public  expiryDate: string,
  )
  {
    this.id=id;
    this.provider=provider;
    this.code=code;
    this.category=category;
    this.description=description;
    this.expiryDate=expiryDate;
}

}
