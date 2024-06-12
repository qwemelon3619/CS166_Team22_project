DROP TABLE IF EXISTS Worker_User_updates;
DROP TABLE IF EXISTS Worker_Catalog_updates;
DROP TABLE IF EXISTS Worker_TrackingInfo_updates;
DROP TABLE IF EXISTS Worker_RentalOrder_updates;
DROP TABLE IF EXISTS User_Catalog_views;
DROP TABLE IF EXISTS Worker;
DROP TABLE IF EXISTS Customer;
CREATE TABLE Worker (
    login CHAR(15) NOT NULL,
    PRIMARY KEY (login),
    FOREIGN KEY (login) REFERENCES Users(login) ON DELETE CASCADE
);

CREATE TABLE Customer (
    login CHAR(15) NOT NULL,
    PRIMARY KEY (login),
    FOREIGN KEY (login) REFERENCES Users(login) ON DELETE CASCADE
);
CREATE TABLE User_Catalog_views (
    login CHAR(15) NOT NULL,
    gameID CHAR(15) NOT NULL,
    PRIMARY KEY (login,gameID),
    FOREIGN KEY (login) REFERENCES Users(login) ON DELETE CASCADE,
    FOREIGN KEY (gameID) REFERENCES Catalog(gameID) ON DELETE CASCADE
);

CREATE TABLE Worker_User_updates (
    login_U CHAR(15) NOT NULL,
    login_W CHAR(15) NOT NULL,
    PRIMARY KEY (login_U,login_W),
    FOREIGN KEY (login_U) REFERENCES Users(login) ON DELETE CASCADE,
    FOREIGN KEY (login_W) REFERENCES Worker(login) ON DELETE CASCADE
);

CREATE TABLE Worker_Catalog_updates (
    login CHAR(15) NOT NULL,
    gameID CHAR(15) NOT NULL,
    PRIMARY KEY (login,gameID),
    FOREIGN KEY (login) REFERENCES Users(login) ON DELETE CASCADE,
    FOREIGN KEY (gameID) REFERENCES Catalog(gameID) ON DELETE CASCADE
);

CREATE TABLE Worker_TrackingInfo_updates (
    login CHAR(15) NOT NULL,
    trackingID CHAR(50) NOT NULL,
    PRIMARY KEY (login,trackingID),
    FOREIGN KEY (login) REFERENCES Users(login) ON DELETE CASCADE,
    FOREIGN KEY (trackingID) REFERENCES TrackingInfo(trackingID) ON DELETE CASCADE
);
CREATE TABLE Worker_RentalOrder_updates (
    login CHAR(15) NOT NULL,
    rentalOrderID char(60) NOT NULL,
    PRIMARY KEY (login,rentalOrderID),
    FOREIGN KEY (login) REFERENCES Users(login) ON DELETE CASCADE,
    FOREIGN KEY (rentalOrderID) REFERENCES RentalOrder(rentalOrderID) ON DELETE CASCADE
);

