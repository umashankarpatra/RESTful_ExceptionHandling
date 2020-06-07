USE [postit]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

/****** Object:  Table [dbo].[web_link]  ******/
CREATE TABLE [dbo].[web_link](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[version] [bigint] NOT NULL,
	[url] [varchar](max) NOT NULL,
	[title] [varchar](255) NOT NULL,
	[fav_count] [bigint] NOT NULL,
	[created_on] [datetime2](7) NULL,
	[enabled] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[tags]  ******/
CREATE TABLE [dbo].[tags](
	[link_id] [bigint] NOT NULL,
	[tag] [varchar](255) NULL
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[tags]  WITH CHECK ADD  CONSTRAINT [fk_tags_link_id] FOREIGN KEY([link_id])
REFERENCES [dbo].[web_link] ([id])
GO

ALTER TABLE [dbo].[tags] CHECK CONSTRAINT [fk_tags_link_id]
GO

