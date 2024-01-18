CREATE TABLE RealEstates (
                             ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                             OwnerID INT,
                             Name VARCHAR(255),
                             Price DECIMAL(12, 2),
                             Status VARCHAR(20),
                             PropertyType VARCHAR(20),
                             OtherType VARCHAR(50),
                             RoomCount INT,
                             Area DECIMAL(10, 2),
                             Description TEXT,
                             ConstructionYear INT,
                             Floor INT,
                             NumberOfFloors INT,
                             AddressID INT,
                             CreatedAt TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                             UpdatedAt TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (OwnerID) REFERENCES Users(ID),
                             FOREIGN KEY (AddressID) REFERENCES Addresses(ID)
);

CREATE TABLE Users (
                       ID INT PRIMARY KEY,
                       FirstName VARCHAR(255),
                       LastName VARCHAR(255),
                       Email VARCHAR(255),
                       Phone VARCHAR(30),
                       Status VARCHAR(20)
);

CREATE TABLE Addresses (
                           ID INT PRIMARY KEY,
                           Country VARCHAR(255),
                           Region VARCHAR(255),
                           District VARCHAR(255),
                           City VARCHAR(255),
                           RegionInCity VARCHAR(255),
                           Street VARCHAR(255),
                           HouseNumber VARCHAR(10)
);

CREATE TABLE Photos (
                        ID INT PRIMARY KEY,
                        ListingID INT,
                        ImageURL VARCHAR(255),
                        FOREIGN KEY (ListingID) REFERENCES RealEstates(ID)
);
