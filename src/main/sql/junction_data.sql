USE tj_configuration
GO

IF NOT EXISTS(SELECT * FROM tj_classnames WHERE name = 'CardPaymentApiJunction')
	EXEC tj_addregisteredclass 
	'CardPaymentApiJunction', 
	'The CardPaymentApiJunction application is a client junction.',
	'handler',
	'za.co.transactionjunction.cardpaymentapi.MainVerticle'
GO
